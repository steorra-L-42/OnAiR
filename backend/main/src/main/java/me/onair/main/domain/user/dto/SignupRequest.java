package me.onair.main.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SignupRequest {

    @Size(min = 1, max = 40)
    @NotBlank(message = "username is required")
    private String username;

    @Size(min = 1, max = 25)
    @NotBlank(message = "password is required")
    private String password;

    @Size(min = 1, max = 25)
    @NotBlank(message = "nickname is required")
    private String nickname;

    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d+", message = "Phone number must contain only digits")
    private String phoneNumber;

    @Size(min = 6, max = 6, message = "Verification code must be 6 characters")
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "\\d+", message = "Verification code must contain only digits")
    private String verification;

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}