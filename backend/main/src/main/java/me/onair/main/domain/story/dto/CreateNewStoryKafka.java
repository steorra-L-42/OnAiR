package me.onair.main.domain.story.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.story.entity.StoryMusic;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 직렬화에서 제외
public class CreateNewStoryKafka {
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private String fcmToken;
    private String storyTitle;
    private String storyContent;
    private Long storyId;
    private Music storyMusic;

    public static CreateNewStoryKafka of(String fcmTokenValue, Story story, StoryMusic storyMusic) {
        if (storyMusic != null) {
            return CreateNewStoryKafka.builder()
                    .fcmToken(fcmTokenValue)
                    .storyTitle(story.getTitle())
                    .storyContent(story.getContent())
                    .storyId(story.getId())
                    .storyMusic(Music.from(storyMusic))
                    .build();
        }
        return CreateNewStoryKafka.builder()
                .fcmToken(fcmTokenValue)
                .storyTitle(story.getTitle())
                .storyContent(story.getContent())
                .storyId(story.getId())
                .build();
    }

    public String toJson() {
        return gson.toJson(this);
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    private static class Music {
        private String storyMusicTitle;
        private String storyMusicArtist;
        private String storyMusicCoverUrl;

        private static Music from(StoryMusic storyMusic) {
            return Music.builder()
                    .storyMusicTitle(storyMusic.getTitle())
                    .storyMusicArtist(storyMusic.getArtist())
                    .storyMusicCoverUrl(storyMusic.getCoverUrl())
                    .build();
        }
    }


}
