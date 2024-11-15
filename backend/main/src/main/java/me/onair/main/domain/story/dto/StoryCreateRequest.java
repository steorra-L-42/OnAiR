package me.onair.main.domain.story.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.story.annotation.ValidMusic;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoryCreateRequest {

    @NotBlank(message = "title is required")
    @Size(max = 50, message = "title must be less than 50 characters")
    private String title;

    @NotBlank(message = "content is required")
    @Size(max = 400, message = "content must be less than 400 characters")
    private String content;

    @ValidMusic
    private Music music;

    @Getter
    public static class Music {

        private String musicTitle; // 노래 제목
        private String musicArtist; // 아티스트 이름
        private String musicCoverUrl; // 앨범 커버 URL
    }
}
