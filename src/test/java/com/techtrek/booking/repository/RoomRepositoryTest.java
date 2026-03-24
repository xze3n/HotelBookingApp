package com.techtrek.booking.repository;

import com.techtrek.booking.config.ApplicationConfig;
import com.techtrek.booking.config.SecurityConfiguration;
import com.techtrek.booking.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        excludeFilters = {@ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfiguration.class
        ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = ApplicationConfig.class
                )
        }
)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.ANY)
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAvailableRooms_ShouldReturnAvailableRooms_WhenThereAreNoBookings(){
        Room room = createRoom("1", RoomType.DOUBLE, 2);
        entityManager.persist(room);
        entityManager.flush();

        LocalDate startDate = LocalDate.of(2026, 5, 10);
        LocalDate endDate = LocalDate.of(2026, 5, 12);

        List<Room> result = roomRepository.findAvailableRooms(startDate, endDate, null, null);

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getRoomNumber());
    }

    @Test
    void findAvailableRooms_ShouldExcludeRoom_WhenBookingOverlaps(){
        Room room = createRoom("2", RoomType.DOUBLE, 2);
        User user = createUser("name@address.com");
        Booking booking = createBooking(user, room,
                LocalDate.of(2026,5,10),
                LocalDate.of(2026,5,15));

        entityManager.persist(user);
        entityManager.persist(room);
        entityManager.persist(booking);
        entityManager.flush();

        LocalDate startDate = LocalDate.of(2026, 5, 12);
        LocalDate endDate = LocalDate.of(2026, 5, 14);

        List<Room> result = roomRepository.findAvailableRooms(startDate, endDate, null, null);

        assertTrue(result.isEmpty());

    }

    @Test
    void findAvailableRooms_ShouldReturnRoom_WhenBookingDoesNotOverlap(){
        Room room = createRoom("3", RoomType.DOUBLE, 2);
        User user = createUser("name2@address.com");
        Booking booking = createBooking(user, room,
                LocalDate.of(2026,5,1),
                LocalDate.of(2026,5,5));

        entityManager.persist(user);
        entityManager.persist(room);
        entityManager.persist(booking);
        entityManager.flush();

        LocalDate startDate = LocalDate.of(2026, 5, 10);
        LocalDate endDate = LocalDate.of(2026, 5, 12);

        List<Room> result = roomRepository.findAvailableRooms(startDate, endDate, null, null);

        assertEquals(1, result.size());
        assertEquals("3", result.get(0).getRoomNumber());
    }

    @Test
    void findAvailableRooms_ShouldFilterByRoomType(){
        Room room1 = createRoom("4", RoomType.SINGLE, 1);
        Room room2 = createRoom("5", RoomType.DOUBLE, 2);

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 3);

        List<Room> result = roomRepository.findAvailableRooms(startDate, endDate, RoomType.DOUBLE, null);

        assertEquals(1, result.size());
        assertEquals("5", result.get(0).getRoomNumber());
        assertEquals(RoomType.DOUBLE, result.get(0).getType());
    }

    @Test
    void findAvailableRooms_ShouldFilterByCapacity(){
        Room room1 = createRoom("6", RoomType.SINGLE, 1);
        Room room2 = createRoom("7", RoomType.SUITE, 3);

        entityManager.persist(room1);
        entityManager.persist(room2);
        entityManager.flush();

        LocalDate startDate = LocalDate.of(2026, 6, 10);
        LocalDate endDate = LocalDate.of(2026, 6, 12);

        List<Room> result = roomRepository.findAvailableRooms(startDate, endDate,null,3);

        assertEquals(1, result.size());
        assertEquals("7", result.get(0).getRoomNumber());
        assertEquals(3, result.get(0).getCapacity());
    }


    private Room createRoom(String roomNumber, RoomType type, int capacity){
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setCapacity(capacity);
        room.setPricePerNight(new BigDecimal("150.00"));
        room.setDescription("Test room");
        room.setCreatedAt(LocalDateTime.now());
        return room;
    }

    private User createUser(String email){
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private Booking createBooking(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate){
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setCreatedAt(LocalDate.now());
        booking.setTotalPrice(new BigDecimal("300.00"));

        return booking;
    }

}
