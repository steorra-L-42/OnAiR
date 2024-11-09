package me.onair.main.domain.fcm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.onair.main.MainApplication;
import me.onair.main.domain.fcm.repository.FcmRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ContextConfiguration(classes = MainApplication.class)
@AutoConfigureMockMvc
public class FcmSaveTest {

    private static final Logger log = LoggerFactory.getLogger(FcmSaveTest.class);

    private final MockMvc mockMvc;
    private final FcmRepository fcmRepository;
    private final UserRepository userRepository;

    @Autowired
    public FcmSaveTest(MockMvc mockMvc, FcmRepository fcmRepository, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.fcmRepository = fcmRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        fcmRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        fcmRepository.deleteAll();
    }

    @Test
    public void 성공() throws Exception {
        // given
        final String url = "/api/v1/user/fcm-token";
        final String requestBody = """
                {
                    "fcmToken": "test"
                }
                """;

        User user = userRepository.save(User.builder()
                .username("username")
                .nickname("nickname")
                .password("password")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .build());

        CustomUserDetails customUserDetails = new CustomUserDetails(user, user.getId(), "profilePath");
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
        // SecurityContextHolder 세션에 인증 정보를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
        assertThat(userRepository.findByUsername("username").get().getFcmToken().getValue()).isEqualTo("test");
        assertThat(fcmRepository.findAll().size()).isEqualTo(1);
    }
}
