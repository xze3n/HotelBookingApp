package com.techtrek.booking.config;

import com.techtrek.booking.entity.*;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import com.techtrek.booking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    @SuppressWarnings("RedundantThrows")
    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (roomRepository.count() > 0) return;
        seedUsers();
        seedRooms();
        seedBookings();
    }

    private void seedBookings() {

        List<User> users = userRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        if (users.isEmpty()) {
            throw new EntityNotFoundException("Required users are missing.");
        }
        if (rooms.isEmpty()) {
            throw new EntityNotFoundException("Required rooms are missing.");
        }

        for (int i=1;i<=20;i++){
            Booking booking = new Booking();
            // User creation
            User randomUser = users.get(
                    faker.number()
                            .numberBetween(0,users.size())
            );
            // Room creation
            Room randomRoom = rooms.get(
                    faker.number().numberBetween(0,rooms.size())
            );
            // CheckIn
            LocalDate checkIn = faker.date()
                            .future(60, TimeUnit.DAYS)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
            ;
            // CheckOut
            LocalDate checkOut = checkIn.plusDays(faker.number().numberBetween(1,8));
            // Nights
            long nights = ChronoUnit.DAYS.between(checkIn,checkOut);
            // Total Price
            BigDecimal totalPrice = randomRoom.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            // Status
            BookingStatus[] statuses = BookingStatus.values();
            BookingStatus randomStatus = statuses[faker.number()
                    .numberBetween(0,statuses.length)];
            // CreatedAt
            LocalDate createdAt = checkIn.minusDays(faker.number().numberBetween(0,10));

            booking.setUser(randomUser);
            booking.setRoom(randomRoom);
            booking.setCheckInDate(checkIn);
            booking.setCheckOutDate(checkOut);
            booking.setTotalPrice(totalPrice);
            booking.setStatus(randomStatus);
            booking.setCreatedAt(createdAt);
            bookingRepository.save(booking);
        }
    }

    private void seedRooms() {
        for (int i = 1; i <= 40; ++i){
            Room room = new Room();
            // ROOM NUMBER
            room.setRoomNumber("Room " + i);
            // TYPE
            RoomType[] types = RoomType.values();
            RoomType assignedType = types[faker.number().numberBetween(0,types.length)];
            room.setType(assignedType);
            // CAPACITY & PRICE PER NIGHT
            switch(assignedType) {
                case SINGLE -> {
                    room.setCapacity(1);
                    room.setPricePerNight(BigDecimal.valueOf(faker.number().randomDouble(2, 300, 700)));
                }
                case DOUBLE -> {
                    room.setCapacity(2);
                    room.setPricePerNight(BigDecimal.valueOf(faker.number().randomDouble(2, 700, 1400)));
                }
                case SUITE -> {
                    room.setCapacity(faker.number().numberBetween(3, 5)); // GENERATES 3 OR 4
                    room.setPricePerNight(BigDecimal.valueOf(faker.number().randomDouble(2, 1400, 2000)));
                }
            }

            // DESCRIPTION
            room.setDescription(
                    "Info: " + room.getRoomNumber()
                            + " , Type: " + room.getType()
                            + " , Capacity: " + room.getCapacity()
                            + " , PricePerNight: " + room.getPricePerNight()
            );
            // CREATED AT
            room.setCreatedAt(
                    faker.date()
                            .past(3650, TimeUnit.DAYS)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );

            roomRepository.save(room);
        }
    }

    private void seedUsers() {

        for (int i = 1;i <= 20;i++){
            User user = new User();

            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(("password123")));
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setRole((i%10 == 0) ? Role.ADMIN : Role.CUSTOMER);
            user.setCreatedAt(faker.date()
                    .past(3650, TimeUnit.DAYS)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            );
            userRepository.save(user);
        }

    }
}
