package com.example.b_food_ordering.Controller;

import com.example.b_food_ordering.Dto.BookingDTO;
import com.example.b_food_ordering.Entity.Booking;
import com.example.b_food_ordering.Service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Người dùng đặt bàn
    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createBooking(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody BookingDTO dto) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Booking booking = bookingService.createBooking(dto, userDetails.getUsername());
        return ResponseEntity.ok(toDTO(booking));
    }

    // Người dùng xem lịch sử đặt bàn
    @GetMapping("/history")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        List<BookingDTO> bookings = bookingService.getUserBookings(userDetails.getUsername());
        return ResponseEntity.ok(bookings);
    }

    // Admin xem tất cả đơn đặt bàn
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Admin xem chi tiết đơn đặt bàn
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.getBookingById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin xác nhận đơn đặt bàn
    @PutMapping("/confirm/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.confirmBooking(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin hủy đơn đặt bàn
    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin xóa đơn đặt bàn
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Chuyển từ Entity sang DTO
    private BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getFullName(),
                booking.getPhoneNumber(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getNumberOfGuests(),
                booking.getArea(),
                booking.getSpecialRequests(),
                booking.getCreatedAt(),
                booking.getStatus().name(),
                booking.getUser().getUsername()
        );
    }
}