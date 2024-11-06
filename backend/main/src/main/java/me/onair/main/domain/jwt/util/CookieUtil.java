package me.onair.main.domain.jwt.util;

import jakarta.servlet.http.Cookie;
import me.onair.main.domain.jwt.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${spring.jwt.refresh.cookie.path}")
    private String REFRESH_TOKEN_COOKIE_PATH;

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(TokenType.REFRESH.getExpireTime().intValue());
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        return cookie;
    }
}