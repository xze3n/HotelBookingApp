package com.techtrek.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="rooms", uniqueConstraints = {
        @UniqueConstraint(name="uk_room_room_number", columnNames = "room_number")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "room_sequence")
    @SequenceGenerator(
            name = "room_sequence",
            sequenceName = "room_sequence",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

}

