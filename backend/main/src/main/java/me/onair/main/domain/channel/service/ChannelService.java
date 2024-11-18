package me.onair.main.domain.channel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.dto.ChannelInfoResponse;
import me.onair.main.domain.channel.dto.ChannelListResponse;
import me.onair.main.domain.channel.dto.CreateNewChannelKafka;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.dto.CreateNewChannelResponse;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.entity.Dj;
import me.onair.main.domain.channel.entity.Track;
import me.onair.main.domain.channel.error.ChannelNotCreated;
import me.onair.main.domain.channel.error.ChannelNotFoundException;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.channel.repository.DjRepository;
import me.onair.main.domain.channel.repository.TrackRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.global.error.ErrorCode;
import me.onair.main.kafka.enums.Topics;
import me.onair.main.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {


    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final DjRepository djRepository;
    private final TrackRepository trackRepository;

    private final KafkaProducer kafkaProducer;

    @Value("${channel.max.size}")
    private int maxChannelSize;

    @Transactional
    public CreateNewChannelResponse createNewChannel(
            CreateNewChannelRequest request,
            CustomUserDetails userDetails
    ) throws JsonProcessingException {

        long channelCount = channelRepository.countByIsEnded(false);
        if (channelCount >= maxChannelSize) {
            log.error("유효 채널이 5개 이상입니다.");
            throw new ChannelNotCreated(ErrorCode.CHANNEL_NOT_CREATED);
        }

        // 1. 유저 정보 얻기
        User user = userRepository.findById(userDetails.getId()).orElseThrow(NotExistUserException::new);
        String fcmToken = user.getFcmToken().getValue();

        // 2. 채널 저장
        Channel channel = Channel.createChannel(request, user);
        channelRepository.save(channel);

        // 3. DJ 저장
        Dj dj = Dj.createDj(request, channel);
        djRepository.save(dj);

        // 4. 플리 저장
        List<Track> trackList = Track.createTrackList(request.getTrackList(), channel);
        trackRepository.saveAll(trackList);

        // 5. 카프카 전송
        CreateNewChannelKafka kafkaMessage = CreateNewChannelKafka.of(channel, dj, trackList, fcmToken);
        kafkaProducer.sendMessageToKafka(
                Topics.CHANNEL_INFO,
                channel.getUuid(),
                kafkaMessage.toJson()
        );
        return CreateNewChannelResponse.from(channel);
    }

    @Transactional(readOnly = true)
    public ChannelInfoResponse getChannelInfo(String channelId) {
        Channel channel = channelRepository.findByUuid(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));

        return ChannelInfoResponse.from(channel);
    }

    @Transactional(readOnly = true)
    public ChannelListResponse getChannelList() {
        List<Channel> channelList = channelRepository.findByIsEnded(false); // 현재 진행 중인 방송 출력
        return ChannelListResponse.from(channelList);
    }

    @Transactional()
    public void stopChannel(String channelUuid) {
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));

        log.info("uuid: {}", channelUuid);
        log.info("channel: {}", channel.toString());
        channel.endChannel();
    }
}
