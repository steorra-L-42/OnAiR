package me.onair.main.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.onair.main.MainApplication;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.domain.user.error.SMSException;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.domain.user.repository.VerificationCodeRepository;
import me.onair.main.domain.user.service.SMSService;
import me.onair.main.global.error.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ContextConfiguration(classes = MainApplication.class)
@AutoConfigureMockMvc
public class RequestVerificationCodeTest {

    private static final Logger log = LoggerFactory.getLogger(RequestVerificationCodeTest.class);

    private final MockMvc mockMvc;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;

    @MockBean
    private SMSService smsService;

    @Autowired
    public RequestVerificationCodeTest(MockMvc mockMvc,
                                       VerificationCodeRepository verificationCodeRepository,
                                       UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
        Mockito.reset(smsService);
    }

    @AfterEach
    public void tearDown() {
        verificationCodeRepository.deleteAll();
        userRepository.deleteAll();
        Mockito.reset(smsService);
    }

    @Nested
    @DisplayName("실패 - 400 Bad Request")
    class Fail400 {

        @Test
        void 전화번호가_비어있음() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": ""
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 전화번호가_공백을_포함() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": "010 1234 5678"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 전화번호가_null() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": null
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 전화번호가_10자리_미만() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": "123456789"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 전화번호가_11자리_초과() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": "123456789012"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 전화번호가_숫자가_아님() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String requestBody = """
                    {
                        "phoneNumber": "abcdefghijk"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(verificationCodeRepository.findAll()).isEmpty();
        }

        @Test
        void 같은_번호로_하루에_5번_초과_요청() throws Exception {
            // given
            final String url = "/api/v1/user/phone-verification/verification-code";
            final String phoneNumber = "01012345678";
            final String requestBody = """
                        {
                            "phoneNumber": "%s"
                        }
                        """.formatted(phoneNumber);
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post(url).with(csrf())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody));
            }
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            // then
            result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.VERIFICATION_CODE_REQUEST_EXCEED_LIMIT.getCode()));
            assertThat(verificationCodeRepository.findAll()).hasSize(5);
        }
    }

    @Test
    @DisplayName("실패 - 409 Conflict - 이미 가입한 전화번호")
    void 이미_가입한_전화번호() throws Exception {
        // given
        final String url = "/api/v1/user/phone-verification/verification-code";
        final String phoneNumber = "01012345678";

        userRepository.save(User.builder()
                .nickname("nickname")
                .username("username")
                .password("password")
                .role(Role.ROLE_USER)
                .phoneNumber(phoneNumber)
                .build());

        final String requestBody = """
                {
                    "phoneNumber": "%s"
                }
                """.formatted(phoneNumber);
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));
        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.DUPLICATE_PHONE_NUMBER.getCode()));
        assertThat(verificationCodeRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("실패 - 500 Internal Server Error - SMS 전송 실패")
    void SMS_전송_실패() throws Exception {
        // given
        Mockito.doThrow(new SMSException()).when(smsService).sendSMS(Mockito.anyString(), Mockito.anyString());
        final String url = "/api/v1/user/phone-verification/verification-code";
        final String requestBody = """
                    {
                        "phoneNumber": "01012345678"
                    }
                    """;
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.SMS_EXCEPTION.getCode()));
        assertThat(verificationCodeRepository.findAll()).isEmpty();
    }

   @Test
    @DisplayName("성공 - 200 OK - 인증번호 요청 성공")
    void 인증번호_요청_성공() throws Exception {
        // given
        final String url = "/api/v1/user/phone-verification/verification-code";
        final String phoneNumber = "01012345678";
        final String requestBody = """
                {
                    "phoneNumber": "%s"
                }
                """.formatted(phoneNumber);
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));
        // then
        result.andExpect(status().isOk());
        assertThat(verificationCodeRepository.findAll()).hasSize(1);
    }
}
