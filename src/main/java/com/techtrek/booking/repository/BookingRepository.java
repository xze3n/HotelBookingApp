package com.techtrek.booking.repository;

import com.techtrek.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookingRepository extends JpaRepository<Booking, Long>{

}
