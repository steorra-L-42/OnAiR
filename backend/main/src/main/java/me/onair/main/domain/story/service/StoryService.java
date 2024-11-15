package me.onair.main.domain.story.service;

import static me.onair.main.global.error.ErrorCode.CHANNEL_MISMATCH;
import static me.onair.main.global.error.ErrorCode.CHANNEL_NOT_FOUND;
import static me.onair.main.global.error.ErrorCode.ENDED_CHANNEL;
import static me.onair.main.global.error.ErrorCode.STORY_NOT_FOUND;
import static me.onair.main.global.error.ErrorCode.STORY_REPLY_ALREADY_EXIST;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.error.ChannelMismatchException;
import me.onair.main.domain.channel.error.ChannelNotFoundException;
import me.onair.main.domain.channel.error.EndedChannelException;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.story.dto.CreateNewStoryKafka;
import me.onair.main.domain.story.dto.StoryCreateRequest;
import me.onair.main.domain.story.dto.StoryCreateRequest.Music;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.story.entity.StoryMusic;
import me.onair.main.domain.story.error.StoryNotFoundException;
import me.onair.main.domain.story.error.StoryReplyAlreadyExistException;
import me.onair.main.domain.story.repository.StoryMusicRepository;
import me.onair.main.domain.story.repository.StoryRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.kafka.dto.StoryReplyDto;
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

    // KafKa Story Reply Consume
    @Transactional
    public void addStoryReply(String key, StoryReplyDto storyReplyDto) {
        log.info("StoryService.addStoryReply: {}", storyReplyDto);

        Channel channel = getChannelByUuid(key); // 채널 검증
        Story story = getStoryById(storyReplyDto.getStoryId()); // 존재하는 사연인지 검증
        validateStoryReply(story); // 사연의 답변이 이미 있는지 검증
        validateStoryChannel(story, channel); // 사연의 채널이 Key값의 채널과 같은지 검증

        story.saveReply(storyReplyDto.getStoryReply()); // 사연의 답변 저장
    }

    // 채널 find, 검증
    private Channel getChannelByUuid(String key) {
        return channelRepository.findByUuid(key)
                .orElseThrow(() -> new ChannelNotFoundException(CHANNEL_NOT_FOUND));
    }

    // 사연 find, 검증
    private Story getStoryById(Long storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(STORY_NOT_FOUND));
    }

    // 사연 답변 검증
    private void validateStoryReply(Story story) {
        if (story.getReply() != null) {
            throw new StoryReplyAlreadyExistException(STORY_REPLY_ALREADY_EXIST);
        }
    }

    // key값의 Channel, Story와 연결된 Channel이 같은지 검증
    private void validateStoryChannel(Story story, Channel channel) {
        String storyChannelUuid = story.getChannel().getUuid();
        if (!storyChannelUuid.equals(channel.getUuid())) {
            throw new ChannelMismatchException(CHANNEL_MISMATCH);
        }
    }
}
