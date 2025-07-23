package com.example.b_food_ordering.Service;

import com.example.b_food_ordering.Dto.OrderDTO;
import com.example.b_food_ordering.Dto.OrderItemDTO;
import com.example.b_food_ordering.Entity.*;
import com.example.b_food_ordering.Repository.OrderRepository;
import com.example.b_food_ordering.Repository.ProductRepository;
import com.example.b_food_ordering.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public OrderDTO createOrder(Long userId, String deliveryAddress, LocalDateTime deliveryDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Cart cart = cartService.getCartEntity(userId);
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống");
        }

        Order order = new Order();
        order.setUser(user);
        order.setFullname(user.getFullname());
        order.setEmail(user.getEmail());
        order.setPhoneNumber(user.getPhoneNumber());
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(deliveryDate);
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PENDING");
        
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getDiscountedPrice() > 0 ? product.getDiscountedPrice() : product.getOriginalPrice());
            orderItem.updateSubtotal();
            orderItems.add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId); 

        return convertToDTO(savedOrder);
    }
    
    @Transactional
    public List<OrderDTO> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return orderRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!isValidOrderStatus(newStatus)) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ");
        }
        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO updatePaymentStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!isValidPaymentStatus(newStatus)) {
            throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ");
        }
        order.setPaymentStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if (!order.getOrderStatus().equals("PENDING") && !order.getOrderStatus().equals("CONFIRMED")) {
            throw new IllegalArgumentException("Chỉ có thể hủy đơn hàng ở trạng thái Chờ xác nhận hoặc Đã xác nhận");
        }
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setFullname(order.getFullname());
        orderDTO.setEmail(order.getEmail());
        orderDTO.setPhoneNumber(order.getPhoneNumber());
        orderDTO.setDeliveryAddress(order.getDeliveryAddress());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setDeliveryDate(order.getDeliveryDate());
        orderDTO.setPaymentStatus(order.getPaymentStatus());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        return orderDTO;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setProductId(orderItem.getProduct().getId());
        orderItemDTO.setProductName(orderItem.getProduct().getName());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setProductImage(orderItem.getProduct().getImg());
        orderItemDTO.setUnitPrice(orderItem.getUnitPrice());
        orderItemDTO.setSubtotal(orderItem.getSubtotal());
        return orderItemDTO;
    }

    private boolean isValidOrderStatus(String status) {
        return List.of("PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED").contains(status);
    }

    private boolean isValidPaymentStatus(String status) {
        return List.of("PENDING", "PAID", "FAILED", "REFUNDED").contains(status);
    }
}