package com.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String city;
    private String district;
    private String street;
    private String unit;
    private String fullAddress;
    private Double lat;
    private Double lng;
}
