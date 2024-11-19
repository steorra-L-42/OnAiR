package me.onair.main.domain.channel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.enums.NewsTopic;
import me.onair.main.domain.channel.enums.Personality;
import me.onair.main.domain.channel.enums.TtsEngine;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dj")
public class Dj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "tts_engine")
    private TtsEngine ttsEngine;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "personality")
    private Personality personality;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "news_topic")
    private NewsTopic newsTopic;

    @OneToOne(mappedBy = "dj")
    private Channel channel;

    public void setChannel(Channel channel) {
        if (this.channel != null) {
            this.channel.changeDj(null);
        }
        this.channel = channel;
        channel.changeDj(this);
    }

    @Builder
    private Dj(TtsEngine ttsEngine, Personality personality, NewsTopic newsTopic) {
        this.ttsEngine = ttsEngine;
        this.personality = personality;
        this.newsTopic = newsTopic;
    }

    public static Dj createDj(CreateNewChannelRequest request, Channel channel) {
        Dj dj = Dj.builder()
            .ttsEngine(request.getTtsEngine())
            .personality(request.getPersonality())
            .newsTopic(request.getNewsTopic())
            .build();
        dj.setChannel(channel);
        return dj;
    }


}
