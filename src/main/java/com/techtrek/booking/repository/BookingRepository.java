package com.techtrek.booking.repository;

import com.techtrek.booking.entity.Booking;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    // All reservations of a user;
    List<Booking> findByUser(User user);

    // All reservations that are overlapping with the given interval for a room;
    List<Booking> findByRoomAndCheckOutDateAfterAndCheckInDateBeforeAndStatus(
            Room room,
            LocalDate checkIn,
            LocalDate checkOut,
            com.techtrek.booking.entity.BookingStatus status
    );

    // Method for Upcoming Bookings
    List<Booking> findByCheckInDateAfter(LocalDate now);
}
