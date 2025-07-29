package com.example.b_food_ordering.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.b_food_ordering.Config.JwtUtil;
import com.example.b_food_ordering.Dto.LoginDTO;
import com.example.b_food_ordering.Dto.RegisterDTO;
import com.example.b_food_ordering.Entity.User;
import com.example.b_food_ordering.Service.UserService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Validated
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        User user = userService.registerUser(
            registerDTO.getUsername(),
            registerDTO.getPassword(),
            registerDTO.getEmail(),
            registerDTO.getFullname(),
            registerDTO.getAddress(),
            registerDTO.getPhoneNumber(),
            "USER"
        );
        return ResponseEntity.ok("Đăng ký người dùng thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
            String storedPassword = userService.findByUsername(loginDTO.getUsername()).getPassword();
            
            if (passwordEncoder.matches(loginDTO.getPassword(), storedPassword)) {
                Set<String> roles = userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toSet());
                String token = jwtUtil.generateToken(loginDTO.getUsername(), roles);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Thông tin đăng nhập không hợp lệ");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("Thông tin đăng nhập không hợp lệ");
        }
    }
}