package me.onair.main.kafka.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StoryReplyDto {

    private TypecastDto typecast;
    private String storyTitle;
    private String fcmToken;
    private Long storyId;
    private StoryMusicDto storyMusic;

    @Getter
    @ToString
    public static class TypecastDto {
        private String text;
        private String actor;
        private String emotionTonePreset;
        private int volume;
        private double speedX;
        private double tempo;
        private int pitch;
        private int lastPitch;
    }

    @Getter
    @ToString
    public static class StoryMusicDto {
        private String storyMusicTitle;
        private String storyMusicArtist;
        private String storyMusicCoverUrl;
    }
}
