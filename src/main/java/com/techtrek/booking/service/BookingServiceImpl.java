package com.techtrek.booking.service;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.entity.Booking;
import com.techtrek.booking.entity.BookingStatus;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.User;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import com.techtrek.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;


    @Override
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
