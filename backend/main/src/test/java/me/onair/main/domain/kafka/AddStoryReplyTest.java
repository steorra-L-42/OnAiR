package me.onair.main.domain.kafka;

import static me.onair.main.util.RequestUtil.createNewChannelRequest;
import static me.onair.main.util.RequestUtil.createSignupRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.story.repository.StoryRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.kafka.enums.Topics;
import me.onair.main.kafka.enums.Topics.NAMES;
import me.onair.main.kafka.producer.KafkaProducer;
import me.onair.main.util.SecurityTestUtil;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {NAMES.STORY_REPLY})
public class AddStoryReplyTest {

    private static KafkaAdmin kafkaAdmin;

    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Mock
    private CustomUserDetails customUserDetails;

    @BeforeAll
    static void setUpAll(ApplicationContext applicationContext) {
        kafkaAdmin = applicationContext.getBean(KafkaAdmin.class);
        kafkaAdmin.createOrModifyTopics(new NewTopic(Topics.STORY_REPLY.getName(), 1, (short) 1));
    }

    @BeforeEach
    void setUp() {
        // 필요한 토픽 생성
        storyRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        storyRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[OK] Add Story Reply")
    void 올바르게_답변이_저장되는_경우() throws InterruptedException {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = userRepository.save(User.createNormalUser(signupRequest));
        SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

        CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
        Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        Story story = Story.of("사연 제목", "사연 내용");
        story.setChannel(channel);
        storyRepository.save(story);

        String key = channel.getUuid();
        String value = String.format("""
                {
                    "typecast": {
                        "text": "합성할 텍스트",
                        "actor": "세나",
                        "emotion_tone_preset": "happy-1",
                        "volume": 100,
                        "speed_x": 1.0,
                        "tempo": 1.0,
                        "pitch": 0,
                        "last_pitch": 0
                    },
                    "fcm_token": "sample_fcm_token",
                    "story_title": "사연 제목",
                    "story_id": "%d",
                    "story_music": {
                        "story_music_title": "Beautiful Day1",
                        "story_music_artist": "U1",
                        "story_music_cover_url": "http://example.com/cover1.jpg"
                    }
                }
                """, story.getId());

        // 메시지 전송
        kafkaProducer.sendMessageToKafka(Topics.STORY_REPLY, key, value);

        // 잠시 대기하여 Kafka 메시지 처리 시간 제공
        Thread.sleep(2000);

        // then: 메시지가 성공적으로 처리되었는지 확인
        Story updatedStory = storyRepository.findById(story.getId()).orElseThrow();
        assertEquals("합성할 텍스트", updatedStory.getReply());
    }

    @Test
    @DisplayName("[Not Found] Add Story Reply - 존재하지 않는 채널")
    void 존재하지_않는_채널인_경우() throws InterruptedException {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = userRepository.save(User.createNormalUser(signupRequest));
        SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

        CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
        Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        Story story = Story.of("사연 제목", "사연 내용");
        story.setChannel(channel);
        storyRepository.save(story);

        String key = "이상한UUID";
        String value = String.format("""
                {
                    "typecast": {
                        "text": "합성할 텍스트",
                        "actor": "세나",
                        "emotion_tone_preset": "happy-1",
                        "volume": 100,
                        "speed_x": 1.0,
                        "tempo": 1.0,
                        "pitch": 0,
                        "last_pitch": 0
                    },
                    "fcm_token": "sample_fcm_token",
                    "story_title": "사연 제목",
                    "story_id": "%d",
                    "story_music": {
                        "story_music_title": "Beautiful Day1",
                        "story_music_artist": "U1",
                        "story_music_cover_url": "http://example.com/cover1.jpg"
                    }
                }
                """, story.getId());

        // when: 메시지 전송
        kafkaProducer.sendMessageToKafka(Topics.STORY_REPLY, key, value);

        Thread.sleep(2000);

        // then: reply 확인
        assertNull(story.getReply());
    }

    @Test
    @DisplayName("[Not Found] Add Story Reply - 존재하지 않는 사연")
    void 존재하지_않는_사연인_경우() throws InterruptedException {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = userRepository.save(User.createNormalUser(signupRequest));
        SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

        CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
        Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        Story story = Story.of("사연 제목", "사연 내용");
        story.setChannel(channel);
        storyRepository.save(story);

        String key = channel.getUuid();
        String value = String.format("""
                {
                    "typecast": {
                        "text": "합성할 텍스트",
                        "actor": "세나",
                        "emotion_tone_preset": "happy-1",
                        "volume": 100,
                        "speed_x": 1.0,
                        "tempo": 1.0,
                        "pitch": 0,
                        "last_pitch": 0
                    },
                    "story_title": "사연 제목",
                    "fcm_token": "sample_fcm_token",
                    "story_id": "%d",
                    "story_music": {
                        "story_music_title": "Beautiful Day1",
                        "story_music_artist": "U1",
                        "story_music_cover_url": "http://example.com/cover1.jpg"
                    }
                }
                """, 123_456_789L);

        // when: 메시지 전송
        kafkaProducer.sendMessageToKafka(Topics.STORY_REPLY, key, value);

        Thread.sleep(2000);

        // then: reply 확인
        assertNull(story.getReply());
    }

    @Test
    @DisplayName("[Not Found] Add Story Reply - 답변이 이미 있는 사연")
    void 답변이_이미_있는_사연인_경우() throws InterruptedException {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = userRepository.save(User.createNormalUser(signupRequest));
        SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

        CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
        Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        Story story = Story.of("사연 제목", "사연 내용");
        story.setChannel(channel);
        story.saveReply("답변 답변 답변 답변 답변");
        storyRepository.save(story);

        String key = channel.getUuid();
        String value = String.format("""
                {
                    "typecast": {
                        "text": "합성할 텍스트",
                        "actor": "세나",
                        "emotion_tone_preset": "happy-1",
                        "volume": 100,
                        "speed_x": 1.0,
                        "tempo": 1.0,
                        "pitch": 0,
                        "last_pitch": 0
                    },
                    "fcm_token": "sample_fcm_token",
                    "story_title": "사연 제목",
                    "story_id": "%d",
                    "story_music": {
                        "story_music_title": "Beautiful Day1",
                        "story_music_artist": "U1",
                        "story_music_cover_url": "http://example.com/cover1.jpg"
                    }
                }
                """, 123_456_789L);

        // when: 메시지 전송
        kafkaProducer.sendMessageToKafka(Topics.STORY_REPLY, key, value);

        Thread.sleep(2000);

        // then: reply 확인
        assertEquals(story.getReply(), "답변 답변 답변 답변 답변");
    }

    @Test
    @DisplayName("[Not Found] Add Story Reply - 채널이 일치하지 않는 사연")
    void 채널이_일치하지_않는_사연인_경우() throws InterruptedException {
        // given
        SignupRequest signupRequest = createSignupRequest();
        User user = userRepository.save(User.createNormalUser(signupRequest));
        SecurityTestUtil.setUpMockUser(customUserDetails, user.getId());

        CreateNewChannelRequest createNewChannelRequest = createNewChannelRequest();
        Channel channel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        Story story = Story.of("사연 제목", "사연 내용");
        story.setChannel(channel);
        storyRepository.save(story);

        Channel otherChannel = channelRepository.save(Channel.createChannel(createNewChannelRequest, user));

        String key = otherChannel.getUuid();
        String value = String.format("""
                {
                    "typecast": {
                        "text": "합성할 텍스트",
                        "actor": "세나",
                        "emotion_tone_preset": "happy-1",
                        "volume": 100,
                        "speed_x": 1.0,
                        "tempo": 1.0,
                        "pitch": 0,
                        "last_pitch": 0
                    },
                    "fcm_token": "sample_fcm_token",
                    "story_title": "사연 제목",
                    "story_id": "%d",
                    "story_music": {
                        "story_music_title": "Beautiful Day1",
                        "story_music_artist": "U1",
                        "story_music_cover_url": "http://example.com/cover1.jpg"
                    }
                }
                """, 123_456_789L);

        // when: 메시지 전송
        kafkaProducer.sendMessageToKafka(Topics.STORY_REPLY, key, value);

        Thread.sleep(2000);

        // then: reply 확인
        assertNull(story.getReply());
    }
}