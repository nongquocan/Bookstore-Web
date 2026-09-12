package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private String addressCity;
    private String addressDistrict;
    private String addressStreet;
    private String addressUnit;
    private String addressFull;
    private Double addressLat;
    private Double addressLng;
    private Set<String> roles;
}
