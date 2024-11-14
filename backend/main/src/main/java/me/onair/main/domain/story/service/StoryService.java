package me.onair.main.domain.story.service;

import static me.onair.main.global.error.ErrorCode.CHANNEL_NOT_FOUND;
import static me.onair.main.global.error.ErrorCode.ENDED_CHANNEL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.error.ChannelNotFoundException;
import me.onair.main.domain.channel.error.EndedChannelException;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.story.dto.CreateNewStoryKafka;
import me.onair.main.domain.story.dto.StoryCreateRequest;
import me.onair.main.domain.story.dto.StoryCreateRequest.Music;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.story.entity.StoryMusic;
import me.onair.main.domain.story.repository.StoryMusicRepository;
import me.onair.main.domain.story.repository.StoryRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.kafka.enums.Topics;
import me.onair.main.kafka.producer.KafkaProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoryService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final StoryMusicRepository storyMusicRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public void createStory(Long channelId, CustomUserDetails customUserDetails, StoryCreateRequest request) {
        log.info("StoryService.createStory: {}", request);

        // 1. 채널 검증
        validateChannelId(channelId);

        // 2. 사용자 및 채널 조회
        User user = getUserById(customUserDetails.getId());
        Channel channel = getChannelById(channelId);

        // 3. 스토리 생성 및 저장
        Story story = createStoryEntity(request, user, channel);

        // 4. 신청곡 처리 -> 생성 및 저장
        StoryMusic storyMusic = processStoryMusic(request, story);

        // 5. KafKa Produce
        produceRecord(story, storyMusic, channel);
    }

    // 채널 ID 검증
    private void validateChannelId(Long channelId) {
        Channel channel = getChannelById(channelId);

        // 방송이 끝난 채널인지 검사
        if (channel.getIsEnded()) {
            throw new EndedChannelException(ENDED_CHANNEL);
        }
    }

    // 사용자 find
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);
    }

    // 채널 find
    private Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(CHANNEL_NOT_FOUND));
    }

    // 스토리 생성
    private Story createStoryEntity(StoryCreateRequest request, User user, Channel channel) {
        Story story = Story.of(request.getTitle(), request.getContent());
        story.setUser(user);
        story.setChannel(channel);

        storyRepository.save(story);
        log.info("Story created and saved: {}", story);

        return story;
    }

    // 신청곡 처리
    private StoryMusic processStoryMusic(StoryCreateRequest request, Story story) {
        if (request.getMusic() != null) {
            return createStoryMusic(request.getMusic(), story);
        }
        return null;
    }

    // 신청곡 생성
    private StoryMusic createStoryMusic(Music music, Story story) {
        String title = music.getMusicTitle();
        String artist = music.getMusicArtist();
        String coverUrl = music.getMusicCoverUrl();
        StoryMusic storyMusic = StoryMusic.of(title, artist, coverUrl);
        storyMusic.setStory(story);  // 여기서 연결

        storyMusicRepository.save(storyMusic);
        log.info("StoryMusic created and saved: {}", storyMusic);

        return storyMusic;
    }

    // KafKa produce
    private void produceRecord(Story story, StoryMusic storyMusic, Channel channel) {
        CreateNewStoryKafka kafkaMessage = CreateNewStoryKafka.of(story, storyMusic);
        kafkaProducer.sendMessageToKafka(
                Topics.STORY,
                channel.getUuid(),
                kafkaMessage.toJson()
        );
    }
}
