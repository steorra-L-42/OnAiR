package me.onair.main.domain.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.enums.NewsTopic;
import me.onair.main.domain.channel.enums.Personality;
import me.onair.main.domain.channel.enums.TtsEngine;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateNewChannelRequest {

    // DJ 관련
    @NotBlank(message = "ttsEngine is required")
    private TtsEngine ttsEngine;

    @NotBlank(message = "personality is required")
    private Personality personality;

    @NotBlank(message = "newsTopic is required")
    private NewsTopic newsTopic;

    // 채널 관련
    private MultipartFile thumbnail;

    // 플레이리스트
    private List<CreateNewChannelTrackRequest> trackList;

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    public static class CreateNewChannelTrackRequest {

        @NotBlank(message = "title is required")
        @Size(min = 1, max = 150, message = "Title must be between 1 and 150 characters")
        private String title;

        @NotBlank(message = "artist is required")
        @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
        private String artist;

        private MultipartFile cover;
    }
}
