package me.onair.main.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NicknameChangeRequest {

    @Size(min = 1, max = 25)
    @NotBlank(message = "nickname is required")
    private String nickname;

    public NicknameChangeRequest(String nickname) {
        this.nickname = nickname;
    }
}
