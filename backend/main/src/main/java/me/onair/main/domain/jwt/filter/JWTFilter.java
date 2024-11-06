package me.onair.main.domain.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import me.onair.main.domain.jwt.enums.TokenType;
import me.onair.main.domain.jwt.util.JWTUtil;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.global.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

// 로그인 후 보내는 요청에 대한 JWT 검증 필터
// fiter chain에 추가할 JWT 검증 필터
// JWT 토큰을 검증하고 정상적인 토큰인 경우 SecurityContextHolder에 일시적인 세션을 생성
// 해당 세션은 Stateless 상태로 관리되어 해당 요청이 끝나면 사라짐
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(TokenType.ACCESS.getHeader());

        // token이 없거나 비어있는 경우
        if(token == null || token.isBlank()){
            log.info("Request without Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 만료되었는지 확인
        token = token.trim();
        try{
            jwtUtil.isExpired(token);
        }catch (ExpiredJwtException e){
            log.error("JWTFilter : Access Token is Expired");
            setResponse(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
            return;
        }

        // 토큰의 카테고리가 access인지 확인
        String category = jwtUtil.getCategory(token);
        if(!category.equals(TokenType.ACCESS.getCategory())){
            log.error("JWTFilter : Invalid Token Category");
            setResponse(response, ErrorCode.WRONG_CATEGORY_JWT);
            return;
        }

        // 사용자 정보를 담은 CustomUserDetails 생성
        User user = User.builder()
                .username(jwtUtil.getUsername(token))
                .password("temppassword") // 비밀번호는 필요없으므로 임시로 설정
                .role(Role.valueOf(jwtUtil.getRole(token)))
                .nickname(jwtUtil.getNickname(token))
                .build();
        Long userId = jwtUtil.getUserId(token);
        String profilePath = jwtUtil.getProfilePath(token);

        CustomUserDetails customUserDetails = new CustomUserDetails(user, userId, profilePath);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // SecurityContextHolder 세션에 인증 정보를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
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