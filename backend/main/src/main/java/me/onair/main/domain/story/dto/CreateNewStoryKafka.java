package me.onair.main.domain.story.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
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
    private static final Gson gson = new Gson();

    private String storyTitle;
    private String storyContent;
    private Long storyId;
    private Music music;

    public static CreateNewStoryKafka of(Story story, StoryMusic storyMusic) {
        if (storyMusic != null) {
            return CreateNewStoryKafka.builder()
                    .storyTitle(story.getTitle())
                    .storyContent(story.getContent())
                    .storyId(story.getId())
                    .music(Music.from(storyMusic))
                    .build();
        }
        return CreateNewStoryKafka.builder()
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
        private String playListMusicTitle;
        private String playListMusicArtist;
        private String playListMusicCoverUrl;

        private static Music from(StoryMusic storyMusic) {
            return Music.builder()
                    .playListMusicTitle(storyMusic.getTitle())
                    .playListMusicArtist(storyMusic.getArtist())
                    .playListMusicCoverUrl(storyMusic.getCoverUrl())
                    .build();
        }
    }


}
