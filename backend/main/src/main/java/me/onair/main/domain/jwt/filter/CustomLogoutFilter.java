package me.onair.main.domain.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import me.onair.main.domain.jwt.enums.TokenType;
import me.onair.main.domain.jwt.repository.RefreshRepository;
import me.onair.main.domain.jwt.util.JWTUtil;
import me.onair.main.global.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

@Builder
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(CustomLogoutFilter.class);
    private static final Pattern LOGOUT_PATTERN = Pattern.compile("^/api/v1/user/logout$");
    private static final String POST_METHOD = "POST";

    private final RefreshRepository refreshRepository;
    private final String REFRESH_TOKEN_COOKIE_PATH;
    private final JWTUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 로그아웃 경로와 다르거나 POST 요청이 아닌 경우
        if (isNotLogoutRequest(request)) {
            log.info("Not Logout Request");
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshTokenFromCookies(request.getCookies());

        // RefreshToken이 null이거나
        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("RefreshToken is null or empty");
            setResponse(response, ErrorCode.NO_REFRESH_TOKEN_COOKIE);
            return;
        }

        refreshToken = refreshToken.trim();
        try{
            jwtUtil.isExpired(refreshToken);
        }catch (ExpiredJwtException e){
            log.error("JWTFilter : Refresh Token is Expired");
            response.setStatus(ErrorCode.REFRESH_TOKEN_EXPIRED.getStatus().value());
            return;
        }

        // RefreshToken의 category 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals(TokenType.REFRESH.getCategory())) {
            log.error("RefreshToken category is not REFRESH");
            setResponse(response, ErrorCode.WRONG_CATEGORY_JWT);
            return;
        }

        // DB에 저장된 RefreshToken인지 확인
        Boolean isExist = refreshRepository.existsByValue(refreshToken);
        if(!isExist) {
            log.error("refresh token not exist : {}", refreshToken);
            setResponse(response, ErrorCode.NOT_EXIST_REFRESH_TOKEN);
            return;
        }

        // 로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshRepository.deleteByValue(refreshToken);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie(TokenType.REFRESH.getHeader(), "");
        cookie.setMaxAge(0);
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isNotLogoutRequest(HttpServletRequest request) {
        return !(LOGOUT_PATTERN.matcher(request.getRequestURI()).matches() && POST_METHOD.equals(request.getMethod()));
    }

    private String getRefreshTokenFromCookies(Cookie[] cookies) {

        // 쿠키가 하나도 없을 경우 null을 반환하도록 한다.
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TokenType.REFRESH.getHeader())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("""
                {
                    "message": "%s",
                    "code": "%s"
                }
            """, errorCode.getMessage(), errorCode.getCode()));
    }
}