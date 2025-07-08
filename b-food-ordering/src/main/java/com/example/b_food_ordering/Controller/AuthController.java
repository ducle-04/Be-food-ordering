package com.example.b_food_ordering.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.b_food_ordering.Config.JwtUtil;
import com.example.b_food_ordering.Entity.User;
import com.example.b_food_ordering.Service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");

        // Kiểm tra đầu vào
        if (username == null || password == null || email == null || 
            username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Các trường không được để trống");
        }

        User user = userService.registerUser(username, password, email, "USER");
        return ResponseEntity.ok("Đăng ký người dùng thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        System.out.println("Login attempt - Username: " + username);
        System.out.println("Login attempt - Password: " + password);
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập và mật khẩu không được để trống");
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails - Username: " + userDetails.getUsername() + ", Password: " + userDetails.getPassword());
            String storedPassword = userService.findByUsername(username).getPassword();
            System.out.println("Stored password from UserService: " + storedPassword);
            System.out.println("Matches result: " + passwordEncoder.matches(password, storedPassword));
            
            if (passwordEncoder.matches(password, storedPassword)) {
                Set<String> roles = userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toSet());
                String token = jwtUtil.generateToken(username, roles);
                System.out.println("Generated token: " + token);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Thông tin đăng nhập không hợp lệ");
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("Username not found: " + e.getMessage());
            return ResponseEntity.status(401).body("Thông tin đăng nhập không hợp lệ");
        }
    }
}
