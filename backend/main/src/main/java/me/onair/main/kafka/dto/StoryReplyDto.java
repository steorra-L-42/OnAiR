package me.onair.main.kafka.dto;

import lombok.Getter;

@Getter
public class StoryReplyDto {

    private String storyReply;
    private Long storyId;
    private StoryMusicDto storyMusic;

    @Getter
    public static class StoryMusicDto {
        private String playListMusicTitle;
        private String playListMusicArtist;
        private String playListMusicCoverUrl;
    }
}

