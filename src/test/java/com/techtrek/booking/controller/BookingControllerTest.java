package com.techtrek.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.entity.Booking;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.RoomType;
import com.techtrek.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith({MockitoExtension.class})
public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;
    private BookingResponse mockResponse;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        mockResponse = new BookingResponse(
                1L,
                "101",
                RoomType.SINGLE,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "ACTIVE"
        );

    }

}
