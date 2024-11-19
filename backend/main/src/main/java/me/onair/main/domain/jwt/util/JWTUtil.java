package me.onair.main.domain.jwt.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import me.onair.main.domain.jwt.entity.RefreshToken;
import me.onair.main.domain.jwt.enums.TokenType;
import me.onair.main.domain.jwt.repository.RefreshRepository;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JWTUtil {

    private final SecretKey SECRET_KEY;

    // application.properties에서 설정한 값을 주입
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    public JWTUtil(@Value("${spring.jwt.secret}") String secretKey, RefreshRepository refreshRepository,
                   UserRepository userRepository) {
        // SecretKey 생성
        // SecretKeySpec 생성자는 바이트 배열과 알고리즘 이름을 사용하여 SecretKey 객체를 생성.
        // SecretKey는 JWT 토큰의 서명과 검증에 사용됩니다.
        SECRET_KEY = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SIG.HS256.key().build().getAlgorithm());
        this.refreshRepository = refreshRepository;
        this.userRepository = userRepository;
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("username", String.class); // 토큰을 파싱하여 username을 반환
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("role", String.class); // 토큰을 파싱하여 role을 반환
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("userId", Long.class); // 토큰을 파싱하여 userId를 반환
    }

    public String getNickname(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("nickname", String.class); // 토큰을 파싱하여 nickname을 반환
    }

    public String getProfilePath(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("profilePath", String.class); // 토큰을 파싱하여 profilePath를 반환
    }

    // 만료 유무
    public void isExpired(String token) {
        Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().getExpiration();
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build() // SECRET_KEY로 토큰을 검증하는 파서 생성
                .parseSignedClaims(token).getPayload().get("category", String.class); // 토큰을 파싱하여 category를 반환
    }

    public String createJwt(TokenType tokenType, String username, String role, Long userId,
                            String nickname, String profilePath) {
        return Jwts.builder()
                .claim("category", tokenType.getCategory()) // access or refresh
                .claim("userId", userId)
                .claim("username", username)
                .claim("role", role)
                .claim("nickname", nickname)
                .claim("profilePath", profilePath)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenType.getExpireTime()))
                .signWith(SECRET_KEY)
                .compact(); // 토큰 생성
    }

    @Transactional
    public void saveRefreshToken(String username, String refresh) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(NotExistUserException::new);

        Date expiredDate = new Date(System.currentTimeMillis() + TokenType.REFRESH.getExpireTime());
        RefreshToken newRefresh = RefreshToken.builder()
                .user(user)
                .value(refresh)
                .expiration(expiredDate.toString())
                .build();

        refreshRepository.save(newRefresh);
    }

    @Transactional
    public void deleteAllRefreshToken(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(NotExistUserException::new);

        refreshRepository.deleteAllByUser(user);
    }
}