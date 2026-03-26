package com.techtrek.booking.service;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.entity.Booking;
import com.techtrek.booking.entity.BookingStatus;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.User;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import com.techtrek.booking.repository.UserRepository;
import com.techtrek.booking.exception.booking.*;
import com.techtrek.booking.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    @Override
    //CREATE BOOKING
    public BookingResponse createBooking(CreateBookingRequest request) {
        if(!request.getCheckOutDate().isAfter(request.getCheckInDate())){
            throw new InvalidDateRangeException("Check-out date must be after check-in date");
        }

        List<Room> rooms = roomRepository.findByType(request.getRoomType());

        for (Room room : rooms) {
            List<Booking> overlappingBookings = bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBeforeAndStatus(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate(),
                    BookingStatus.ACTIVE
            );

            overlappingBookings.removeIf(b -> b.getStatus() == BookingStatus.CANCELLED);

            if (overlappingBookings.isEmpty()) {

                Booking booking = new Booking();
                booking.setUser(getCurrentUser());
                booking.setRoom(room);
                booking.setCheckInDate(request.getCheckInDate());
                booking.setCheckOutDate(request.getCheckOutDate());
                booking.setStatus(BookingStatus.ACTIVE);

                long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
                BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(days));
                booking.setTotalPrice(totalPrice);

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponse(savedBooking);
            }
        }
        throw new BookingConflictException("There are no " + request.getRoomType()+ " rooms available for the selected dates.");
    }


    //RETURN ALL RESERVATIONS
    @Override
    public List<BookingResponse> getAllBookings(){
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    //CANCEL A RESERVATION BY ID
    @Override
    public void cancelBooking(Long bookingId) {
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRole() == com.techtrek.booking.entity.Role.ADMIN;
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> isAdmin
                        ? new BookingNotFoundException("Booking with id: " + bookingId + " is not found.")
                        : new UnathorizedBookingAccesException("You are not authorized to cancel this booking."));
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        if(!isOwner && !isAdmin){
            throw new UnathorizedBookingAccesException("You are not authorized to cancel this booking.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED){
            throw new BookingAlreadyCancelledException("Booking with id: " + bookingId + " is already cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
    //UPDATE RESERVATION BY ID
    @Override
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request){
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRole() == com.techtrek.booking.entity.Role.ADMIN;
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> isAdmin
                        ? new BookingNotFoundException("Booking with id: " + bookingId + " is not found.")
                        : new UnathorizedBookingAccesException("You are not authorized to update this booking."));
        boolean isOwner = booking.getUser().getId().equals(currentUser.getId());
        if(!isOwner && !isAdmin){
            throw new UnathorizedBookingAccesException("You are not authorized to update this booking.");
        }
        if(!request.getCheckOutDate().isAfter(request.getCheckInDate())){
            throw new InvalidDateRangeException("Check-out date must be after check-in date.");
        }
        List<Room> rooms = roomRepository.findByType(request.getRoomType());
        Room availableRoom = null;
        for (Room room : rooms){
            List<Booking> overlappingBookings = bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBeforeAndStatus(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate(),
                    BookingStatus.ACTIVE
            );
            overlappingBookings.removeIf(b -> b.getId().equals(bookingId));
            overlappingBookings.removeIf(b -> b.getStatus() == BookingStatus.CANCELLED);
            if (overlappingBookings.isEmpty()){
                availableRoom = room;
                break;
            }
        }
        if (availableRoom == null) {
            throw new BookingConflictException("No available rooms of type " + request.getRoomType() + " for the selected dates.");
        }
        booking.setRoom(availableRoom);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        long updatedDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal updatedTotalPrice = availableRoom.getPricePerNight().multiply(BigDecimal.valueOf(updatedDays));
        booking.setTotalPrice(updatedTotalPrice);
        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    @Override
    public List<BookingResponse> getBookingsForCurrentUser(){
        User currentUser = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUser(currentUser);
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Override
    public BookingResponse getBookingById(Long id){
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id: " + id + " is not found."));
        return mapToResponse(booking);
    }
    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("No authenticated user found.");
        }

        Object principal = authentication.getPrincipal();

        String email;
        if (principal instanceof UserDetails userDetails){
            email = userDetails.getUsername();
        } else if (principal != null){
            email=principal.toString();
        } else {
            throw new UserNotFoundException("No authenticated user found.");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found,"));
    }

    private BookingResponse mapToResponse(Booking booking){
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getRoomNumber(),
                booking.getRoom().getType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus().name()

        );
    }

    @Override
    public List<BookingResponse> getUpcomingBookings() {
        List<Booking> bookings = bookingRepository.findByCheckInDateAfterAndStatus(LocalDate.now(), BookingStatus.ACTIVE);
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
