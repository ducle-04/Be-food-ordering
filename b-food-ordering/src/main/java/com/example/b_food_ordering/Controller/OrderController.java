package com.example.b_food_ordering.Controller;

import com.example.b_food_ordering.Dto.OrderDTO;
import com.example.b_food_ordering.Dto.ResponseDTO;
import com.example.b_food_ordering.Repository.UserRepository;
import com.example.b_food_ordering.Service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
        com.example.b_food_ordering.Entity.User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found for username: {}", username);
            throw new IllegalArgumentException("Người dùng không hợp lệ");
        }
        return user.getId();
    }

    // Người dùng đặt hàng 
    @PostMapping
    public ResponseEntity<ResponseDTO<OrderDTO>> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // Kiểm tra thủ công các trường bắt buộc
            if (orderRequest.getDeliveryAddress() == null || orderRequest.getDeliveryAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
            }
            Long userId = getCurrentUserId();
            logger.info("Creating order for user {} with delivery address: {}", userId, orderRequest.getDeliveryAddress());
            OrderDTO order = orderService.createOrder(userId, orderRequest.getDeliveryAddress(), orderRequest.getDeliveryDate());
            return ResponseEntity.status(201).body(new ResponseDTO<>("Đặt hàng thành công", order));
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO<>("Lỗi: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }

    // Người dùng xem danh sách đơn hàng của mình
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getUserOrders() {
        try {
            Long userId = getCurrentUserId();
            logger.info("Fetching orders for user {}", userId);
            List<OrderDTO> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ResponseDTO<>("Lấy danh sách đơn hàng thành công", orders));
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO<>("Lỗi: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }

    // Admin xem tất cả đơn hàng
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getAllOrders() {
        try {
            logger.info("Admin fetching all orders");
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(new ResponseDTO<>("Lấy danh sách tất cả đơn hàng thành công", orders));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }

    // Admin cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<OrderDTO>> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            logger.info("Admin updating order {} to status {}", id, status);
            OrderDTO order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(new ResponseDTO<>("Cập nhật trạng thái đơn hàng thành công", order));
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO<>("Lỗi: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }
    
    // Admin cập nhật trạng thái thanh toán
    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<OrderDTO>> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            logger.info("Admin updating payment status for order {} to {}", id, status);
            OrderDTO order = orderService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(new ResponseDTO<>("Cập nhật trạng thái thanh toán thành công", order));
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO<>("Lỗi: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }
    // Admin hoặc người dùng hủy đơn hàng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseDTO<Object>> cancelOrder(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            logger.info("Canceling order {} for user {}", id, userId);
            orderService.cancelOrder(id);
            return ResponseEntity.ok(new ResponseDTO<>("Hủy đơn hàng thành công", null));
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseDTO<>("Lỗi: " + e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ResponseDTO<>("Lỗi server: " + e.getMessage(), null));
        }
    }
}

class OrderRequest {
    private String deliveryAddress;
    private LocalDateTime deliveryDate;

    // Getters and setters
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}