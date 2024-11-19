package me.onair.main.domain.story.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name = "story_music")
public class StoryMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "artist", nullable = false, length = 150)
    private String artist;

    @Column(name = "cover_url", nullable = false, columnDefinition = "TEXT")
    private String coverUrl;

    @OneToOne
    @JoinColumn(name = "story_id")
    private Story story;

    private StoryMusic(String title, String artist, String coverUrl) {
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
    }

    public static StoryMusic of(String title, String artist, String coverUrl) {
        return new StoryMusic(title, artist, coverUrl);
    }

    public void setStory(Story story) {
        if (this.story != null) {
            this.story.changeStoryMusic(null);
        }
        this.story = story;
        story.changeStoryMusic(this);
    }

}
