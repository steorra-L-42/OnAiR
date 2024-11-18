package me.onair.main.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class UpdateProfileResponse {

    private String nickname;
    private String url;

    public static UpdateProfileResponse from(String nickname, String url) {
        return new UpdateProfileResponse(nickname, url);
    }
}