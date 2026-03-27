package com.techtrek.booking.service;
import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.entity.*;
import com.techtrek.booking.exception.booking.*;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import com.techtrek.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User testUser;
    private Room testRoom;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("password").
                firstName("Test")
                .lastName("User")
                .role(Role.CUSTOMER)
                .build();

        testUser.setId(1L);
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setType(RoomType.SINGLE);
        testRoom.setCapacity(1);
        testRoom.setPricePerNight(new BigDecimal("100"));
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUser(testUser);
        testBooking.setRoom(testRoom);
        testBooking.setCheckInDate(LocalDate.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDate.now().plusDays(3));
        testBooking.setStatus(BookingStatus.ACTIVE);
        testBooking.setTotalPrice(new BigDecimal("200"));
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(userDetails.getUsername()).thenReturn("test@example.com");
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(testUser));

    }

    @Test
    void createBooking_ShouldReturnBookingResponse() {
        CreateBookingRequest request = new CreateBookingRequest(
                RoomType.SINGLE,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)

        );
        when(roomRepository.findByType(RoomType.SINGLE)).thenReturn(List.of(testRoom));
        when(bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBeforeAndStatus(
                any(Room.class),
                any(LocalDate.class),
                any(LocalDate.class),
                any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.save(any())).thenReturn(testBooking);
        BookingResponse response = bookingService.createBooking(request);
        assertNotNull(response);
        assertEquals("101", response.roomNumber());

    }
    @Test
    void createBooking_ShouldThrow_WhenDatesInvalid() {
        CreateBookingRequest request = new CreateBookingRequest(
                RoomType.SINGLE,
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(1)
        );
        assertThrows(InvalidDateRangeException
                        .class,
                        () -> bookingService.createBooking(request));
    }

    @Test
    void getAllBookings_ShouldReturnList() {
        when(bookingRepository.findAll()).thenReturn(List.of(testBooking));
        List<BookingResponse> result = bookingService.getAllBookings();
        assertEquals(1, result.size());
    }

    @Test
    void cancelBooking_ShouldWork() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        bookingService.cancelBooking(1L);
        assertEquals(BookingStatus.CANCELLED, testBooking.getStatus());
    }

    @Test
    void updateBooking_ShouldWork() {
        BookingUpdateRequest request = new BookingUpdateRequest(
                RoomType.SINGLE,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(roomRepository.findByType(RoomType.SINGLE))
                .thenReturn(new ArrayList<>(List.of(testRoom)));
        when(bookingRepository.findByRoomAndCheckOutDateAfterAndCheckInDateBeforeAndStatus(
                any(Room.class),
                any(LocalDate.class),
                any(LocalDate.class),
                any(BookingStatus.class)))
                .thenReturn(new ArrayList<>());

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BookingResponse response = bookingService.updateBooking(1L, request);
        assertNotNull(response);
        assertEquals("101", response.roomNumber());
        assertEquals(RoomType.SINGLE, response.roomType());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void getBookingsForCurrentUser_ShouldReturnList() {
        when(bookingRepository.findByUser(testUser)).thenReturn(List.of(testBooking));
        List<BookingResponse> result = bookingService.getBookingsForCurrentUser();
        assertEquals(1, result.size());
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        BookingResponse response = bookingService.getBookingById(1L);
        assertNotNull(response);
    }

    @Test
    void getUpcomingBookings_ShouldReturnList() {
        when(bookingRepository.findByCheckInDateAfter(any()))
                .thenReturn(List.of(testBooking));
        List<BookingResponse> result = bookingService.getUpcomingBookings();
        assertEquals(1, result.size());

    }

}
