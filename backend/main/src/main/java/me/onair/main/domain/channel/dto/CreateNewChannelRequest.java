package me.onair.main.domain.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.onair.main.domain.channel.enums.NewsTopic;
import me.onair.main.domain.channel.enums.Personality;
import me.onair.main.domain.channel.enums.TtsEngine;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@ToString
public class CreateNewChannelRequest {

    // DJ 관련
    @NotNull(message = "ttsEngine is required")
    private TtsEngine ttsEngine;

    @NotNull(message = "personality is required")
    private Personality personality;

    @NotNull(message = "newsTopic is required")
    private NewsTopic newsTopic;

    // 채널 관련
    @NotBlank(message = "thumbnail is required")
    @Size(min = 1, max = 2000, message = "thumbnail must be between 1 and 2000 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_:/.]+$", message = "thumbnail can only contain letters, numbers, hyphens (-), and underscores (_).")
    private String thumbnail;

    @NotBlank(message = "channelName is required")
    @Size(min = 1, max = 150, message = "channelName must be between 1 and 150 characters")
    private String channelName;

    // 플레이리스트
    private List<CreateNewChannelTrackRequest> trackList;

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    @ToString
    public static class CreateNewChannelTrackRequest {

        @NotBlank(message = "title is required")
        @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters")
        private String title;

        @NotBlank(message = "artist is required")
        @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
        private String artist;

        @NotBlank(message = "cover is required")
        @Size(min = 1, max = 150, message = "cover must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "cover can only contain letters, numbers, hyphens (-), and underscores (_).")
        private String cover;
    }
}
