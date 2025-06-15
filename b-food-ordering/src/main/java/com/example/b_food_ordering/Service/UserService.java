package com.example.b_food_ordering.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.b_food_ordering.Dto.UserDTO;
import com.example.b_food_ordering.Entity.Role;
import com.example.b_food_ordering.Entity.User;
import com.example.b_food_ordering.Repository.RoleRepository;
import com.example.b_food_ordering.Repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Đăng ký người dùng mới (dùng nội bộ)
    @Transactional
    public User registerUser(String username, String password, String email, String... roleNames) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setEnabled(true);
        user.setRoles(getOrCreateRoles(roleNames));

        return userRepository.save(user);
    }

    // ✅ Lấy tất cả user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Tạo mới user từ UserDTO (dùng cho Admin)
    public User createUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(dto.isEnabled());
        user.setRoles(getOrCreateRoles(dto.getRoles().toArray(new String[0])));

        return userRepository.save(user);
    }

    
    // ✅ Cập nhật thông tin user từ UserDTO (dùng cho Admin)
    @Transactional
    public User updateUser(String username, UserDTO dto) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User không tồn tại");

        user.setEmail(dto.getEmail());
        user.setEnabled(dto.isEnabled());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setRoles(getOrCreateRoles(dto.getRoles().toArray(new String[0])));

        return userRepository.save(user);
    }

    // ✅ Helper: Lấy hoặc tạo roles từ tên
    private Set<Role> getOrCreateRoles(String... roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
            roles.add(role);
        }
        return roles;
    }
}
