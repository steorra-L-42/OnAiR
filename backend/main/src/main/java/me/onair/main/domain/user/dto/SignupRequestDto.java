package me.onair.main.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SignupRequestDto {

    @Size(min = 1, max = 25)
    @NotBlank(message = "username is required")
    private String username;

    @Size(min = 1, max = 25)
    @NotBlank(message = "password is required")
    private String password;

    @Size(min = 1, max = 25)
    @NotBlank(message = "nickname is required")
    private String nickname;

    @Size(min = 1, max = 20)
    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @Size(min=6, max=6)
    @NotBlank(message = "verification is required")
    private String verification;

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}