package com.bookstore.service;

import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.dto.UserDTO;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.EntityNotFoundException;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest loginRequest) throws Exception {
        // Kiểm tra email tồn tại
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + loginRequest.getEmail()));

        // Kiểm tra tài khoản có hoạt động không
        if (!user.getIsActive()) {
            throw new Exception("User account is not active");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new Exception("Invalid email or password");
        }

        // Cập nhật thời gian đăng nhập lần cuối
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Tạo JWT token
        String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());

        // Lấy tên các role
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .success(true)
                .message("Login successful")
                .build();
    }

    public AuthResponse register(RegisterRequest registerRequest) throws Exception {
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new Exception("Password and confirm password do not match");
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new Exception("Email is already registered");
        }

        // Tạo user mới
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setIsActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // Gán role mặc định là USER
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(
                        () -> new EntityNotFoundException("Default USER role not found. Please initialize roles."));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);

        // Lưu user
        newUser = userRepository.save(newUser);

        // Tạo JWT token
        String token = jwtTokenProvider.generateTokenFromEmail(newUser.getEmail());

        Set<String> roleNames = newUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(newUser.getId())
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .roles(roleNames)
                .success(true)
                .message("Registration successful")
                .build();
    }

    public UserDTO getCurrentUser(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .addressCity(user.getAddressCity())
                .addressDistrict(user.getAddressDistrict())
                .addressStreet(user.getAddressStreet())
                .addressUnit(user.getAddressUnit())
                .addressFull(user.getAddressFull())
                .addressLat(user.getAddressLat())
                .addressLng(user.getAddressLng())
                .roles(roles)
                .build();
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new Exception("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public boolean updateAddress(String email, com.bookstore.dto.AddressDTO addressDTO) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        user.setAddressCity(addressDTO.getCity());
        user.setAddressDistrict(addressDTO.getDistrict());
        user.setAddressStreet(addressDTO.getStreet());
        user.setAddressUnit(addressDTO.getUnit());
        user.setAddressFull(addressDTO.getFullAddress());
        user.setAddressLat(addressDTO.getLat());
        user.setAddressLng(addressDTO.getLng());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        return true;
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}

