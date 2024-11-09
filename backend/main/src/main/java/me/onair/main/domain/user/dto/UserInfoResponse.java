package me.onair.main.domain.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class UserInfoResponse {

    private Long user_id;
    private String nickname;
    private String username;
    private String phoneNumber;
    private String profilePath;
    private Role role;

    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getUsername(), user.getPhoneNumber(),
                user.getProfilePath(), user.getRole());
    }
}