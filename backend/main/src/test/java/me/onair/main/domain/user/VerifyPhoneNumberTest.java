package me.onair.main.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import me.onair.main.MainApplication;
import me.onair.main.domain.user.entity.VerificationCode;
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
public class VerifyPhoneNumberTest {

    private static final Logger log = LoggerFactory.getLogger(RequestVerificationCodeTest.class);

    private final MockMvc mockMvc;
    private final VerificationCodeRepository verificationCodeRepository;

    @Autowired
    public VerifyPhoneNumberTest(MockMvc mockMvc, VerificationCodeRepository verificationCodeRepository) {
        this.mockMvc = mockMvc;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @BeforeEach
    public void setUp() {
        verificationCodeRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        verificationCodeRepository.deleteAll();
    }

    @Nested
    @DisplayName("실패 - 400 Bad Request")
    class Fail400 {

        @Test
        void 전화번호가_비어있음() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 전화번호가_공백을_포함() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "010 1234 5678",
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
        }

        @Test
        void 전화번호가_null() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 전화번호가_10자리_미만() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 전화번호가_10자리_초과() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 전화번호가_숫자가_아님() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "010-1234-5678",
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
        }

        @Test
        void 인증번호가_비어있음() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 인증번호가_공백을_포함() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "01012345678",
                        "verification": "123 456"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        void 인증번호가_null() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
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
            result.andExpect(status().isBadRequest());
        }

        @Test
        void 인증번호가_6자리_미만() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

        @Test
        void 인증번호가_6자리_초과() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
        }

    }

    @Nested
    @DisplayName("성공 - 200 OK")
    class Success200 {

        @Test
        void true_인증_성공() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(true));
            assertThat(verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter("01012345678", "123456",
                    LocalDateTime.now()).isVerified()).isTrue();
        }

        @Test
        void true_아직_만료되지않은_이전_인증번호로_인증_요청() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("789012")
                    .expiredAt(LocalDateTime.now().plusMinutes(3))
                    .build());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(true));
            assertThat(verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter("01012345678", "123456",
                    LocalDateTime.now()).isVerified()).isTrue();
            assertThat(verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter("01012345678", "789012",
                    LocalDateTime.now()).isVerified()).isFalse();
        }

        @Test
        void false_인증_요청한_전화번호가_없음() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
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
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(false));
        }

        @Test
        void false_시간_만료() throws Exception {
            // given
            String url = "/api/v1/user/phone-verification";
            String requestBody = """
                    {
                        "phoneNumber": "01012345678",
                        "verification": "123456"
                    }
                    """;

            verificationCodeRepository.save(VerificationCode.builder()
                    .phoneNumber("01012345678")
                    .code("123456")
                    .expiredAt(LocalDateTime.now().minusMinutes(999))
                    .build());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(false));
            assertThat(verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter("01012345678", "123456",
                    LocalDateTime.now().minusMinutes(9999)).isVerified()).isFalse();
        }
    }
}