package com.example.b_food_ordering.Repository;

import com.example.b_food_ordering.Entity.Booking;
import com.example.b_food_ordering.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUsername(String username);
}