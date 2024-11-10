package me.onair.main.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import me.onair.main.MainApplication;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.entity.VerificationCode;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.domain.user.repository.VerificationCodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ContextConfiguration(classes = MainApplication.class)
@AutoConfigureMockMvc
public class SignupTest {

    private static final Logger log = LoggerFactory.getLogger(RequestVerificationCodeTest.class);

    private final MockMvc mockMvc;

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;

    @Autowired
    public SignupTest(MockMvc mockMvc, VerificationCodeRepository verificationCodeRepository,
                      UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("400 Bad Request")
    class Fail400 {

        @Test
        void username_0글자() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void username_공백() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": " ",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void username_40글자초과() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "12345678901234567890123456789012345678901",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void password_0글자() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void password_공백() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": " ",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void password_25글자초과() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "12345678901234567890123456",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void nickname_0글자() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void nickname_공백() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": " ",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void nickname_25글자초과() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "12345678901234567890123456",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void phoneNumber_비어있음() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void phoneNumber_10글자미만() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "123456789",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void phoneNumber_11글자초과() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "123456789012",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void phoneNumber_공백() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "           ",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void phoneNumber_숫자가아님() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "abcdefghijk",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void verification_비어있음() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": ""
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void verification_6글자미만() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "12345"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void verification_6글자초과() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "1234567"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void verification_공백() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "      "
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void verification_숫자가아님() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "abcdefg"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void 인증_시도한적_없는_전화번호() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("B003"));
            assertThat(userRepository.count()).isZero();
        }

        @Test
        void 인증번호_불일치() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "654321"
                    }
                    """;

            // 인증 후
            VerificationCode verificationCode = verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            verificationCode.verify();
            verificationCodeRepository.save(verificationCode);

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("B003"));
            assertThat(userRepository.count()).isZero();
        }

    }

    @Nested
    @DisplayName("409 Conflict")
    class Fail409 {

        @Test
        void 중복된_username() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // 인증 후
            VerificationCode verificationCode = verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            verificationCode.verify();
            verificationCodeRepository.save(verificationCode);

            // 회원가입 후
            userRepository.save(User.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .phoneNumber("01012345678")
                    .role(Role.ROLE_USER)
                    .build());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("B001"));
            assertThat(userRepository.count()).isOne();
        }

        @Test
        void 이미_가입된_번호() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // 인증 후
            VerificationCode verificationCode = verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            verificationCode.verify();
            verificationCodeRepository.save(verificationCode);

            // 회원가입 후
            userRepository.save(User.builder()
                    .username("username2")
                    .password("password")
                    .nickname("nickname")
                    .phoneNumber("01012345678")
                    .role(Role.ROLE_USER)
                    .build());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("B002"));
            assertThat(userRepository.count()).isOne();
        }
    }

    @Nested
    @DisplayName("200 OK")
    class Success200 {

        @Test
        void 회원가입성공() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // 인증 후

            VerificationCode verificationCode = verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            verificationCode.verify();
            verificationCodeRepository.save(verificationCode);

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk());
            assertThat(userRepository.count()).isOne();
        }

        @Test
        void 성공_verification_만료시간이_지난_verificationcode() throws Exception {
            // given
            final String url = "/api/v1/user/signup";
            final String requestBody = """
                    {
                        "username": "username",
                        "password": "password",
                        "nickname": "nickname",
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            // 인증 후

            VerificationCode verificationCode = verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().minusMinutes(3)) // 만료시간이 지난 상태
                    .build());

            verificationCode.verify();
            verificationCodeRepository.save(verificationCode);

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk());
            assertThat(userRepository.count()).isOne();
        }

    }
}
