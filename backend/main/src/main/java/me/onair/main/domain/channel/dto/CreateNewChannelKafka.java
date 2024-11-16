package me.onair.main.domain.channel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.channel.entity.Dj;
import me.onair.main.domain.channel.entity.Track;
import me.onair.main.domain.channel.enums.NewsTopic;
import me.onair.main.domain.channel.enums.Personality;
import me.onair.main.domain.channel.enums.TtsEngine;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 직렬화에서 제외
public class CreateNewChannelKafka {

  private String fcmToken;
  private Boolean isDefault;
  private TtsEngine ttsEngine;
  private Personality personality;
  private NewsTopic newsTopic;
  private List<TrackInfo> playlist;

  // Jackson ObjectMapper를 사용한 JSON 직렬화 메서드 추가
  public String toJson() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    return objectMapper.writeValueAsString(this);
  }

  @Builder
  private CreateNewChannelKafka(String fcmToken, Boolean isDefault, TtsEngine ttsEngine,
      Personality personality, NewsTopic newsTopic, List<TrackInfo> playlist
  ) {
    this.fcmToken = fcmToken;
    this.isDefault = isDefault;
    this.ttsEngine = ttsEngine;
    this.personality = personality;
    this.newsTopic = newsTopic;
    this.playlist = playlist;
  }

  public static CreateNewChannelKafka of(Channel channel, Dj dj, List<Track> trackList, String fcmToken) {
    return CreateNewChannelKafka.builder()
        .fcmToken(fcmToken)
        .isDefault(channel.getIsDefault())
        .ttsEngine(dj.getTtsEngine())
        .personality(dj.getPersonality())
        .newsTopic(dj.getNewsTopic())
        .playlist(TrackInfo.fromAll(trackList))
        .build();
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class TrackInfo {
    private String playlistMusicTitle;
    private String playlistMusicArtist;
    private String playlistMusicCoverUrl;

    public static TrackInfo from(Track track) {
      return TrackInfo.builder()
          .playlistMusicTitle(track.getTitle())
          .playlistMusicArtist(track.getArtist())
          .playlistMusicCoverUrl(track.getCover())
          .build();
    }

    public static List<TrackInfo> fromAll(List<Track> trackList) {
      List<TrackInfo> trackInfoList = new ArrayList<>();
      for (Track track : trackList) {
        trackInfoList.add(from(track));
      }
      return trackInfoList;
    }
  }
}
