package com.techtrek.booking.config;

import com.techtrek.booking.entity.*;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import com.techtrek.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private final Faker faker = new Faker();

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (roomRepository.count() > 0) return;
        seedUsers();
        seedRooms();
        seedBookings();
    }

    private void seedBookings() {
//        for (long i = 1;i <= 20;i++){
//            Booking booking = new Booking();
//
//
//        }
    }

    private void seedRooms() {
        final int capacity = 6;

        for (int i = 1;i <= 40;i++){
            Room room = new Room();
            // ROOM NUMBER
            room.setRoomNumber("Room " + i);
            // TYPE
            RoomType[] types = RoomType.values();
            room.setType(types[
                    faker.number().numberBetween(0,types.length) ]
            );
            // CAPACITY
            room.setCapacity(faker.number().numberBetween(0,capacity));
            // PRICE PER NIGHT
            room.setPricePerNight(
                    BigDecimal.valueOf(
                            faker.number().randomDouble(
                                    2,
                                    300,
                                    2000
                            )
                    )
            );
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
        }
    }

    private void seedUsers() {

        for (int i = 1;i <= 20;i++){
            User user = new User();

            user.setEmail(faker.internet().emailAddress());
            user.setPassword("password123");
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
