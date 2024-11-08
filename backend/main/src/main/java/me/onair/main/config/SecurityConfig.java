package me.onair.main.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import me.onair.main.domain.jwt.CustomAuthenticationEntryPoint;
import me.onair.main.domain.jwt.enums.TokenType;
import me.onair.main.domain.jwt.filter.CustomLogoutFilter;
import me.onair.main.domain.jwt.filter.JWTFilter;
import me.onair.main.domain.jwt.filter.LoginFilter;
import me.onair.main.domain.jwt.repository.RefreshRepository;
import me.onair.main.domain.jwt.util.CookieUtil;
import me.onair.main.domain.jwt.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private static final String LOGIN_URL = "/api/v1/user/login";
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;
    @Value("${cors.url}")
    private String corsURL;
    @Value("${spring.jwt.refresh.cookie.path}")
    private String REFRESH_TOKEN_COOKIE_PATH;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] allowedOrigins = Arrays.stream(corsURL.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        LoginFilter myLoginFilter = LoginFilter.builder()
                .authenticationManager(authenticationManager(authenticationConfiguration))
                .jwtUtil(jwtUtil)
                .cookieUtil(cookieUtil)
                .build();
        myLoginFilter.setFilterProcessesUrl(LOGIN_URL); // login url을 원하는 url로 변경

        return http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true); // 토큰을 주고받을 때는 credentials를 허용해야 함
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L); // 1시간동안 캐싱

                                // 클라이언트에서 Authorization 헤더를 사용할 수 있도록 설정
                                configuration.setExposedHeaders(
                                        Collections.singletonList(TokenType.ACCESS.getHeader()));

                                return configuration;
                            }
                        })
                )
                .authorizeHttpRequests((auth) -> auth
                        // KafKa Publish Test
                        .requestMatchers(
                                new AntPathRequestMatcher("/kafka/publish/test-topic")
                        ).permitAll()
                        // for actuator
                        .requestMatchers(
                                new AntPathRequestMatcher("/actuator/**")
                        ).permitAll()
                        // /login, /join, / 경로로 들어오는 요청은 인증이 필요하지 않음
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/v1/user/phone-verification/verification-code"),
                                new AntPathRequestMatcher("/api/v1/user/phone-verification"),
                                new AntPathRequestMatcher("/api/v1/user/valid-username"),
                                new AntPathRequestMatcher("/api/v1/user/signup"),
                                new AntPathRequestMatcher("/api/v1/user/login"),
                                new AntPathRequestMatcher("/api/v1/user/reissue")
                        ).permitAll()
                        // /admin 경로로 들어오는 요청은 ADMIN 권한이 필요
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/v1/admin/**")
                        ).hasRole("ADMIN")
                        // 나머지 요청은 인증이 필요
                        .anyRequest().authenticated()
                )

                //  jwt token에서는 csrf 설정을 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // form login, http basic, logout 설정을 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // jwt token 유효성 검사를 위한 필터 추가
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                // login : 커스텀 login filter를 UsernamePasswordAuthenticationFilter와 동일한 위치에 추가
                .addFilterAt(myLoginFilter, UsernamePasswordAuthenticationFilter.class)
                // logout : 커스텀 logout filter 추가
                .addFilterBefore(CustomLogoutFilter.builder()
                                .jwtUtil(jwtUtil)
                                .refreshRepository(refreshRepository)
                                .REFRESH_TOKEN_COOKIE_PATH(REFRESH_TOKEN_COOKIE_PATH)
                                .build()
                        , LogoutFilter.class)

                // jwt token에서는 session 설정을 비활성화
                .sessionManagement((management) -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // /api/** 경로로 들어오는 요청에 대해 인증되지 않은 경우 401 UNAUTHORIZED 응답을 반환
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                //new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new CustomAuthenticationEntryPoint(HttpStatus.UNAUTHORIZED),
                                // log를 찍기 위해 커스텀한 EntryPoint
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .build();
    }

    // 외부에서 AuthenticatinConfiguration을 주입받아서 AuthenticationManager를 생성해서 반환
    // AuthenticationManager는 인증을 처리하는 인터페이스
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}