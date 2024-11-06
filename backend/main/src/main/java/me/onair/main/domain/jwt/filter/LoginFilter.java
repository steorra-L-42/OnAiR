package me.onair.main.domain.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import lombok.Builder;
import me.onair.main.domain.jwt.util.CookieUtil;
import me.onair.main.domain.jwt.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;

    @Builder
    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       CookieUtil cookieUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    // username, password를 받아서 인증
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // username, password를 받아서 토큰을 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 검증
        return authenticationManager.authenticate(authToken);
    }

    // 인증이 성공하면 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) {
        log.info("login success");

        // 유정 정보
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt(TokenType.ACCESS, username, role);
        String refresh = jwtUtil.createJwt(TokenType.REFRESH, username, role);
        jwtUtil.deleteAllRefreshToken(username); // 기존 refresh token 삭제
        jwtUtil.saveRefreshToken(username, refresh);

        // 토큰을 헤더에 담아서 반환
        response.setHeader(TokenType.ACCESS.getHeader(), access); // access token
        response.addCookie(cookieUtil.createCookie(TokenType.REFRESH.getHeader(), refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    // 인증이 실패하면 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("login fail");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}