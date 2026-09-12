package com.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Tên đệm và tên không được để trống")
    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    @Pattern(
        regexp = "^[\\p{L}][\\p{L} ]*[\\p{L}]$|^[\\p{L}]$",
        message = "Tên đệm và tên chỉ được chứa chữ cái, không có số hoặc ký tự đặc biệt"
    )
    private String firstName;

    @NotBlank(message = "Họ không được để trống")
    @Size(max = 100, message = "Họ không được quá 100 ký tự")
    @Pattern(
        regexp = "^[\\p{L}][\\p{L} ]*[\\p{L}]$|^[\\p{L}]$",
        message = "Họ chỉ được chứa chữ cái, không có số hoặc ký tự đặc biệt"
    )
    private String lastName;

    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String phone;
}
