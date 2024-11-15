package me.onair.main.domain.jwt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {

    ACCESS("access", 1000L * 60 * 10, "Authorization"), // 10분
    REFRESH("refresh", 1000L * 60 * 60 * 24, "Refresh"); // 1일

    private final String category;
    private final Long expireTime;
    private final String Header;
}
