package me.onair.main.domain.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.entity.Dj;
import me.onair.main.domain.channel.entity.Track;
import me.onair.main.domain.channel.enums.NewsTopic;
import me.onair.main.domain.channel.enums.Personality;
import me.onair.main.domain.channel.enums.TtsEngine;
import me.onair.main.domain.user.entity.User;


@Getter
@ToString
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelInfoResponse {

  // 개설 유저 정보
  private String userNickname;
  private String profilePath;

  // 채널
  private String channelName;
  private Boolean isDefault;
  private LocalDateTime start;
  private LocalDateTime end;
  private Boolean isEnded;
  private String thumbnail;
  private String channelUuid;

  // DJ
  private TtsEngine ttsEngine;
  private Personality personality;
  private NewsTopic newsTopic;

  // 플리
  private List<TrackInfoResponse> playList;

  public static ChannelInfoResponse from(Channel channel) {
    User user = channel.getUser();
    Dj dj = channel.getDj();
    List<Track> tracks = channel.getTracks();

    return ChannelInfoResponse.builder()
        .userNickname(user.getNickname())
        .profilePath(user.getProfilePath())
        .channelName(channel.getChannelName())
        .isDefault(channel.getIsDefault())
        .start(channel.getStart())
        .end(channel.getEnd())
        .isEnded(channel.getIsEnded())
        .thumbnail(channel.getThumbnail())
        .channelUuid(channel.getUuid())
        .ttsEngine(dj.getTtsEngine())
        .personality(dj.getPersonality())
        .newsTopic(dj.getNewsTopic())
        .playList(TrackInfoResponse.fromAll(tracks))
        .build();
  }


  @Getter
  @ToString
  @Builder(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class TrackInfoResponse{
    private String title;
    private String artist;
    private String cover;

    public static TrackInfoResponse from(Track track) {
      return TrackInfoResponse.builder()
          .title(track.getTitle())
          .artist(track.getArtist())
          .cover(track.getCover())
          .build();
    }

    public static List<TrackInfoResponse> fromAll(List<Track> tracks) {
      List<TrackInfoResponse> trackList = new ArrayList<>();
      for(Track track : tracks){
        trackList.add(from(track));
      }
      return trackList;
    }
  }
}
