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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    @Override
    //CREEARE BOOKING
    public BookingResponse createBooking(CreateBookingRequest request) {

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
        throw new RuntimeException("There are no " + request.getRoomType()+ " rooms available for the selected dates.");
    }   // DE FACUT EXCEPTIE CUSTOM "NoAvailableRoom"

    //RETURNEAZA TOATE REZERVARILE
    @Override
    public List<BookingResponse> getAllBookings(){
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    //ANULEAZA O REZERVARE DUPA ID
    @Override
    public void cancelBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        //DE FACUT EXCEPTIE CUSTOM
        User currentUser = getCurrentUser();
        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not authorized to cancel this booking.");
        }


        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    //UPDATE REZERVARE DUPA ID
    @Override
    public BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        //DE FACUT EXCEPTIE CUSTOM
        User currentUser = getCurrentUser();
        if(!booking.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not authorized to update this booking.");
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
            throw new RuntimeException("No available rooms of type " + request.getRoomType()+ " for the selected dates.");
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
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }
    private User getCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String email;
    if (principal instanceof UserDetails userDetails){
        email = userDetails.getUsername();
    } else{
        email=principal.toString();
    }
    return userRepository.findByEmail(email)
            .orElseThrow(()-> new RuntimeException("User not found"));
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
}
