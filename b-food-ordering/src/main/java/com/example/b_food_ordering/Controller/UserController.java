package com.example.b_food_ordering.Controller;

import com.example.b_food_ordering.Dto.UserDTO;
import com.example.b_food_ordering.Entity.Role;
import com.example.b_food_ordering.Entity.User;
import com.example.b_food_ordering.Service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ 1. Lấy profile người dùng hiện tại
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(user));
    }

 // ✅ 2. Admin lấy danh sách tất cả người dùng (không bao gồm tài khoản ADMIN)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers().stream()
            .filter(user -> user.getRoles().stream()
                .noneMatch(role -> role.getName().equalsIgnoreCase("ADMIN")))
            .collect(Collectors.toList());

        List<UserDTO> dtos = users.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ✅ 3. Admin xem chi tiết 1 user
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(user));
    }

    // ✅ 4. Admin tạo tài khoản mới
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        User user = userService.createUser(dto);
        return ResponseEntity.ok(toDTO(user));
    }

    // ✅ 5. Admin cập nhật thông tin user
    @PutMapping("/update/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody UserDTO dto) {
        User updatedUser = userService.updateUser(username, dto);
        return ResponseEntity.ok(toDTO(updatedUser));
    }

    // ✅ 6. Admin xóa user
    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    // Helper: convert User -> UserDTO
    private UserDTO toDTO(User user) {
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new UserDTO(user.getUsername(), user.getEmail(), roles);
    }
}

