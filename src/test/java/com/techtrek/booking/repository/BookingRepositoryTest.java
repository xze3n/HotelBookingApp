package com.techtrek.booking.repository;

import com.techtrek.booking.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@WithMockUser(roles="ADMIN")
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    private User testUser;
    private Room testRoom;
    private Booking testBooking;

    @BeforeEach
    void setUp(){
        testUser = User.builder()
                .email("booking@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        testRoom = new Room();
        testRoom.setRoomNumber("101");
        testRoom.setType(RoomType.SINGLE);
        testRoom.setCapacity(1);
        testRoom.setPricePerNight(new BigDecimal("100"));
        testRoom.setCreatedAt(LocalDateTime.now());
        testRoom = roomRepository.save(testRoom);

        testBooking = new Booking();
        testBooking.setUser(testUser);
        testBooking.setRoom(testRoom);
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setStatus(BookingStatus.ACTIVE);
        testBooking.setTotalPrice(new BigDecimal("200"));
        testBooking.setCreatedAt(LocalDate.now());
        testBooking = bookingRepository.save(testBooking);
    }

    @AfterEach
    void tearDown(){
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUser_ShouldReturnBookings_WhenUserHasBookings(){
        List<Booking> bookings = bookingRepository.findByUser(testUser);
        assertThat(bookings).hasSize(1);
        String email = bookings.getFirst().getUser().getEmail();
        assertThat(email).isEqualTo("booking@example.com");
    }

    @Test
    void findByUser_ShouldReturnEmpty_WhenUserHasNoBookings(){
        User emptyUser = User.builder()
                .email("empty@example.com")
                .password("password123")
                .firstName("Empty")
                .lastName("Name")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
        emptyUser = userRepository.save(emptyUser);
        List<Booking> bookings = bookingRepository.findByUser(emptyUser);
        assertThat(bookings).isEmpty();
        userRepository.delete(emptyUser);

    }

    @Test
    void findByRoomAndDates_ShouldReturnOverlappingBookings(){
        List<Booking> overlapping = bookingRepository
                .findByRoomAndCheckOutDateAfterAndCheckInDateBefore(
                        testRoom,
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3)
                );
        assertThat(overlapping).hasSize(1);
    }

    @Test
    void findByRoomAndDates_ShouldReturnEmpty_WhenNoOverlap(){
        List<Booking> overlapping = bookingRepository
                .findByRoomAndCheckOutDateAfterAndCheckInDateBefore(
                        testRoom,
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(15)
                );
        assertThat(overlapping).isEmpty();
    }

    @Test
    void findByCheckInDateAfter_ShouldReturnUpcomingBookings(){
        List<Booking> upcoming = bookingRepository.findByCheckInDateAfter(LocalDate.now());
        assertThat(upcoming)
                .extracting(Booking::getId)
                .contains(testBooking.getId());
    }

    @Test
    void findByCheckInDateAfter_ShouldReturnEmpty_WhenNoUpcomingBookings(){
        bookingRepository.deleteAll();
        List<Booking> upcoming = bookingRepository.findByCheckInDateAfter(LocalDate.now());
        assertThat(upcoming).isEmpty();

    }

}
