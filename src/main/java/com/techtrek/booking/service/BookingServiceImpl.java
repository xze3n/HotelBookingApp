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
            List<Booking> overlappingBookings = bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );
            if (overlappingBookings.isEmpty()) {

                Booking booking = new Booking();
                booking.setUser(getCurrentUser());
                booking.setRoom(room);
                booking.setCheckInDate(request.getCheckInDate());
                booking.setCheckOutDate(request.getCheckOutDate());
                booking.setStatus(BookingStatus.ACTIVE);

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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id: " + bookingId + " is not found."));
        if (booking.getStatus() == BookingStatus.CANCELLED){
            throw new BookingAlreadyCancelledException("Booking with id: " + bookingId + " is already cancelled.");
        }
        User currentUser = getCurrentUser();
        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new UnathorizedBookingAccesException("You are not authorized to cancel this booking.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
    //UPDATE RESERVATION BY ID
    @Override
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id: " + bookingId + " is not found."));
        if(!request.getCheckOutDate().isAfter(request.getCheckInDate())){
            throw new InvalidDateRangeException("Check-out date must be after check-in date.");
        }
        User currentUser = getCurrentUser();
        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new UnathorizedBookingAccesException("You are not authorized to update this booking.");
        }
        List<Room> rooms = roomRepository.findByType(request.getRoomType());
        Room availableRoom = null;
        for (Room room : rooms){
            List<Booking> overlappingBookings = bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBefore(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );
            overlappingBookings.removeIf(b ->b.getId().equals(bookingId));
            if (overlappingBookings.isEmpty()){
                availableRoom = room;
                break;
            }
        }
        if (availableRoom == null ) {
            throw new BookingConflictException("No available rooms of type " + request.getRoomType()+ " for the selected dates.");
        }
        booking.setRoom(availableRoom);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
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
        List<Booking> bookings = bookingRepository.findByCheckInDateAfter(LocalDate.now());
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
