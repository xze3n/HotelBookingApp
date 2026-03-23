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
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        testRoom = new Room();
        testRoom.setRoomNumber("101");


    }
}
