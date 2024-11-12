package me.onair.main.domain.channel.service;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.dto.CreateNewChannelResponse;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.entity.Dj;
import me.onair.main.domain.channel.entity.Track;
import me.onair.main.domain.channel.repository.ChannelRepository;
import me.onair.main.domain.channel.repository.DjRepository;
import me.onair.main.domain.channel.repository.TrackRepository;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {


    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final DjRepository djRepository;
    private final TrackRepository trackRepository;

    public CreateNewChannelResponse createNewChannel(
            @Valid CreateNewChannelRequest request,
            CustomUserDetails userDetails) {

        // 1. 유저 정보 얻기
        User user = userRepository.findById(userDetails.getId()).orElseThrow(NotExistUserException::new);

        // 2. 채널 만들기
        // 어드민이면 True(기본 채널), 일반 유저면 False(커스텀 채널)
        Boolean isDefault = (user.getRole() == Role.ROLE_ADMIN);
        // 기본 채널이면 24시간 유지, 커스텀 채널이면 2시간 유지
        LocalDateTime end = isDefault ? LocalDateTime.now().plusDays(1) : LocalDateTime.now().plusHours(2);
        //TODO: 썸네일 구현 필요
        String thumbnail = "";

        Channel channel = Channel.createChannel(isDefault, thumbnail, end);
        channel.changeUser(user);

        // 3. DJ 만들기
        Dj dj = Dj.createDj(request, channel);

        // 4. 플리 만들기
        List<Track> trackList = Track.createTrackList(request.getTrackList(), channel);

        // 저장
        channelRepository.save(channel);
        djRepository.save(dj);
        trackRepository.saveAll(trackList);

        // TODO: 카프카 전송
        return CreateNewChannelResponse.from(channel);
    }
}
