package me.onair.main.domain.channel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.dto.ChannelInfoResponse;
import me.onair.main.domain.channel.dto.CreateNewChannelKafka;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.dto.CreateNewChannelResponse;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.entity.Dj;
import me.onair.main.domain.channel.entity.Track;
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

    @Transactional
    public CreateNewChannelResponse createNewChannel(
            CreateNewChannelRequest request,
            CustomUserDetails userDetails
    ) throws JsonProcessingException {

        // 1. 유저 정보 얻기
        User user = userRepository.findById(userDetails.getId()).orElseThrow(NotExistUserException::new);

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
        CreateNewChannelKafka kafkaMessage = CreateNewChannelKafka.of(channel, dj, trackList);
        kafkaProducer.sendMessageToKafka(
                Topics.CHANNEL_INFO,
                channel.getUuid(),
                kafkaMessage.toJson()
        );
        return CreateNewChannelResponse.from(channel);
    }

    public ChannelInfoResponse getChannelInfo(String channelId) {
        Channel channel = channelRepository.findByUuid(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND));
        return ChannelInfoResponse.from(channel);
    }
}
