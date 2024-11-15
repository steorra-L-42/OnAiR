package me.onair.main.domain.story;

import static me.onair.main.global.error.ErrorCode.CHANNEL_NOT_FOUND;
import static me.onair.main.global.error.ErrorCode.ENDED_CHANNEL;
import static me.onair.main.util.RequestUtil.createNewChannelRequest;
import static me.onair.main.util.RequestUtil.createSignupRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.onair.main.MainApplication;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.fcm.entity.FcmToken;
import me.onair.main.domain.fcm.repository.FcmRepository;
import me.onair.main.domain.story.dto.CreateNewStoryKafka;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.story.entity.StoryMusic;
import me.onair.main.domain.story.repository.StoryMusicRepository;
import me.onair.main.domain.story.repository.StoryRepository;
import me.onair.main.domain.user.CheckDuplicatedUsernameTest;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.kafka.enums.Topics;
import me.onair.main.kafka.producer.KafkaProducer;
import me.onair.main.util.SecurityTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = MainApplication.class)
@AutoConfigureMockMvc
public class CreateStoryTest {

    private static final Logger log = LoggerFactory.getLogger(CheckDuplicatedUsernameTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CustomUserDetails customUserDetails;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private StoryMusicRepository storyMusicRepository;
    @Autowired
    private FcmRepository fcmRepository;
    @MockBean
    private KafkaProducer kafkaProducer;  // KafkaProducer를 모킹

    @BeforeEach
    void setUp() {
        storyMusicRepository.deleteAll();
        storyRepository.deleteAll();
        channelRepository.deleteAll();
        fcmRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        storyMusicRepository.deleteAll();
        storyRepository.deleteAll();
        channelRepository.deleteAll();
        fcmRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("[200 Ok]")
    class ValidRequest {
        @Transactional
        @Test
        void music이_있는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = User.createNormalUser(signupRequest);
            FcmToken fcmToken = FcmToken.from("testFcmToken");
            fcmRepository.save(fcmToken);

            user.setFcmToken(fcmToken);
            userRepository.save(user);
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
            Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

            final String url = "/api/v1/story/" + channel.getUuid();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isOk());
            assertThat(storyRepository.count()).isOne();
            assertThat(storyMusicRepository.count()).isOne();

            Story savedStory = storyRepository.findAll().get(0);  // 첫 번째 저장된 story
            StoryMusic savedStoryMusic = storyMusicRepository.findAll().get(0);  // 첫 번째 저장된 storyMusic
            assertThat(savedStory.getTitle()).isEqualTo("사연 제목 제목 제목");
            assertThat(savedStory.getContent()).isEqualTo("사연 내용 내용 내용");
            assertThat(savedStoryMusic.getTitle()).isEqualTo("Hysteria");
            assertThat(savedStoryMusic.getArtist()).isEqualTo("Muse");
            assertThat(savedStoryMusic.getCoverUrl()).isEqualTo("https://www.example.com");

            CreateNewStoryKafka createNewStoryKafka = CreateNewStoryKafka.of(fcmToken.getValue(), savedStory,
                    savedStoryMusic);
            verify(kafkaProducer, Mockito.times(1)).sendMessageToKafka(Mockito.eq(Topics.STORY),
                    Mockito.eq(channel.getUuid()),
                    Mockito.eq(createNewStoryKafka.toJson()));
        }

        @Transactional
        @Test
        void music이_비어있는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = User.createNormalUser(signupRequest);
            FcmToken fcmToken = FcmToken.from("testFcmToken");
            fcmRepository.save(fcmToken);

            user.setFcmToken(fcmToken);
            userRepository.save(user);
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
            Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

            final String url = "/api/v1/story/" + channel.getUuid();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isOk());
            assertThat(storyRepository.count()).isOne();
            assertThat(storyMusicRepository.count()).isZero();

            Story savedStory = storyRepository.findAll().get(0);  // 첫 번째 저장된 story
            assertThat(savedStory.getTitle()).isEqualTo("사연 제목 제목 제목");
            assertThat(savedStory.getContent()).isEqualTo("사연 내용 내용 내용");

            CreateNewStoryKafka createNewStoryKafka = CreateNewStoryKafka.of(fcmToken.getValue(), savedStory, null);
            verify(kafkaProducer, Mockito.times(1)).sendMessageToKafka(Mockito.eq(Topics.STORY),
                    Mockito.eq(channel.getUuid()),
                    Mockito.eq(createNewStoryKafka.toJson()));
        }
    }

    @Nested
    @DisplayName("[400 Bad Request] Invalid RequestBody")
    class InvalidRequestBody {
        @Transactional
        @Test
        void 사연_title이_Null인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_title이_Empty인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "  ",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_title이_최대길이를_초과하는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = String.format("""
                    {
                        "title": "%s",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """, "김".repeat(200));

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_contents가_Null인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_contents가_Empty인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "   ",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_content가_최대길이를_초과하는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = String.format("""
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "%s",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """, "김".repeat(500));

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicTitle이_Null인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicTitle이_Empty인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "  ",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicTitle이_최대길이를_초과하는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = String.format("""
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "%s",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """, "김".repeat(500));

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicContent가_Null인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicContent가_Empty인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicContent": "  ",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicContent가_최대길이를_초과하는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = String.format("""
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "%s",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """, "김".repeat(500));

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicCoverUrl이_Null인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicCoverUrl이_Empty인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "  "
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }

        @Transactional
        @Test
        void 사연_musicCoverUrl이_http_https로_시작하지_않는_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + user.getId();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "tcp://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Nested
    @DisplayName("[400 Bad Request] Ended Channel")
    class EndedChannel {
        @Transactional
        @Test
        void 종료된_채널인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
            Channel channel = Channel.createChannel(createNewChannelRequest, user);
            channel.endChannel();
            channelRepository.save(channel);

            final String url = "/api/v1/story/" + channel.getUuid();
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code")
                            .value(ENDED_CHANNEL.getCode()));
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }

    @Nested
    @DisplayName("[404 Not Found] Not Found Channel")
    class ChannelNotFound {
        @Transactional
        @Test
        void 없는_채널인_경우() throws Exception {
            // given
            SignupRequest signupRequest = createSignupRequest();
            User user = userRepository.save(User.createNormalUser(signupRequest));
            SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

            final String url = "/api/v1/story/" + 123_456_789L;
            final String requestBody = """
                    {
                        "title": "사연 제목 제목 제목",
                        "content": "사연 내용 내용 내용",
                        "music": {
                            "musicTitle": "Hysteria",
                            "musicArtist": "Muse",
                            "musicCoverUrl": "https://www.example.com"
                        }
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));
            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code")
                            .value(CHANNEL_NOT_FOUND.getCode()));
            assertThat(storyRepository.count()).isZero();
            // Kafka 메시지가 보내지지 않았음을 검증
            verify(kafkaProducer, Mockito.times(0)).sendMessageToKafka(Mockito.any(), Mockito.any(), Mockito.any());
        }
    }
}
