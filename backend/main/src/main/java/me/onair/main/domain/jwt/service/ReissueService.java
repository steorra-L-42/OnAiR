package me.onair.main.domain.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.onair.main.domain.jwt.enums.TokenType;
import me.onair.main.domain.jwt.error.ExpiredRefreshTokenException;
import me.onair.main.domain.jwt.error.NoRefreshTokenCookieException;
import me.onair.main.domain.jwt.error.NotExistRefreshTokenException;
import me.onair.main.domain.jwt.error.WrongCategoryJwtException;
import me.onair.main.domain.jwt.repository.RefreshRepository;
import me.onair.main.domain.jwt.util.CookieUtil;
import me.onair.main.domain.jwt.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReissueService {

    private static final Logger log = LoggerFactory.getLogger(ReissueService.class);

    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;

    public String verifyRefresh(HttpServletRequest request) {

        // refresh token 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TokenType.REFRESH.getHeader())) {
                refresh = cookie.getValue();
            }
        }
        if (refresh == null) {
            throw new NoRefreshTokenCookieException();
        }

        // refresh token 검증
        refresh = refresh.trim();
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new ExpiredRefreshTokenException(e);
        }

        // category 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals(TokenType.REFRESH.getCategory())) {
            throw new WrongCategoryJwtException();
        }

        // DB에 저장된 refresh token인지 확인
        Boolean isExist = refreshRepository.existsByValue(refresh);
        if(!isExist) {
            log.error("refresh token not exist : {}", refresh);
            throw new NotExistRefreshTokenException();
        }

        return refresh;
    }

    public String reissueAccess(String refresh) {

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        Long userId = jwtUtil.getUserId(refresh);
        String nickname = jwtUtil.getNickname(refresh);
        String profilePath = jwtUtil.getProfilePath(refresh);

        // make new access token
        return jwtUtil.createJwt(TokenType.ACCESS, username, role, userId, nickname, profilePath);
    }

    @Transactional
    public Cookie reissueRefresh(String refresh) {

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        Long userId = jwtUtil.getUserId(refresh);
        String nickname = jwtUtil.getNickname(refresh);
        String profilePath = jwtUtil.getProfilePath(refresh);

        // make new refresh token
        String newRefresh = jwtUtil.createJwt(TokenType.REFRESH, username, role, userId, nickname, profilePath);

        // delete old refresh token
        jwtUtil.deleteAllRefreshToken(username);
        // flush
        refreshRepository.flush();
        // save new refresh token
        jwtUtil.saveRefreshToken(username, newRefresh);

        return cookieUtil.createCookie(TokenType.REFRESH.getHeader(), newRefresh);
    }
}