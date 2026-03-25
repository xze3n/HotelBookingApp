package com.techtrek.booking.controller;
import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.entity.RoomType;
import com.techtrek.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception{
        CreateBookingRequest request = new CreateBookingRequest(
                RoomType.SINGLE,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
        when(bookingService.createBooking((any()))).thenReturn(mockResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomType").value("SINGLE"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    @Test
    void getAllBookings_ShouldReturnListOfBookings() throws Exception{
        when(bookingService.getAllBookings()).thenReturn(List.of(mockResponse));
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("101"));

    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception{
        when(bookingService.getBookingById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomType").value("SINGLE"));
    }

    @Test
    void updateBooking_ShouldReturnUpdatedBooking() throws Exception {
        BookingUpdateRequest request = new BookingUpdateRequest(
                RoomType.SINGLE,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );
        when(bookingService.updateBooking(eq(1L), any())).thenReturn(mockResponse);
        mockMvc.perform(put("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void cancelBooking_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookingService).cancelBooking(1L);
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void getMyBookings_ShouldReturnCurrentUserBookings() throws Exception {
        when(bookingService.getBookingsForCurrentUser()).thenReturn(List.of(mockResponse));
        mockMvc.perform(get("/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
    @Test
    void getUpcomingBookings_ShouldReturnUpcomingBookings() throws Exception {
        when(bookingService.getUpcomingBookings()).thenReturn(List.of(mockResponse));
        mockMvc.perform(get("/bookings/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("101"));
    }
}


