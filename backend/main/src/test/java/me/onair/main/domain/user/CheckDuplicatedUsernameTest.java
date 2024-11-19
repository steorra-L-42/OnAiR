package me.onair.main.domain.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.onair.main.MainApplication;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.domain.user.repository.UserRepository;
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
public class CheckDuplicatedUsernameTest {

    private static final Logger log = LoggerFactory.getLogger(CheckDuplicatedUsernameTest.class);

    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @Autowired
    public CheckDuplicatedUsernameTest(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("실패 - 404 Not Found")
    class Fail_404_Not_Found {

        @Test
        void username이_공백인_경우() throws Exception {
            // given
            final String username = "     ";
            final String url = "/api/v1/user/valid-username/" + username;

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("B006"));
        }

        @Test
        void username이_40자를_초과하는_경우() throws Exception {
            // given
            final String username = "a".repeat(41);
            final String url = "/api/v1/user/valid-username/" + username;

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("B006"));
        }
    }

    @Test
    void Fail_404_username이_비어있는_경우() throws Exception {
        // given
        final String username = "";
        final String url = "/api/v1/user/valid-username/" + username;

        // when
        ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNotFound());
    }

    @Nested
    @DisplayName("성공 - 200 OK")
    class Success_200_OK {

        @Test
        void username이_중복되지_않는_경우() throws Exception {
            // given
            final String username = "onair";
            final String url = "/api/v1/user/valid-username/" + username;

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(true));
        }

        @Test
        void username이_중복되는_경우() throws Exception {
            // given
            final String username = "onair";
            final String url = "/api/v1/user/valid-username/" + username;

            userRepository.save(User.builder()
                    .username(username)
                    .nickname("nickname")
                    .password("password")
                    .phoneNumber("01012345678")
                    .role(Role.ROLE_USER)
                    .build());

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(false));
        }
    }

}
