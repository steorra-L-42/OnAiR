package me.onair.main.domain.story.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "story")
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false, length = 400)
    private String content;

    @Column(name = "reply", columnDefinition = "TEXT")
    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "story")
    private StoryMusic storyMusic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    private Story(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Story of(String title, String content) {
        return new Story(title, content);
    }

    public void setUser(User user) {
        if (this.user != null) {
            this.user.getStories().remove(this);
        }
        this.user = user;
        user.getStories().add(this);
    }

    public void setChannel(Channel channel) {
        if (this.channel != null) {
            this.channel.getStories().remove(this);
        }
        this.channel = channel;
        channel.getStories().add(this);
    }

    public void changeStoryMusic(StoryMusic storyMusic) {
        this.storyMusic = storyMusic;
    }

    public void saveReply(String reply) {
        this.reply = reply;
    }

}
