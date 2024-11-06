package me.onair.main.domain.channel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "channel")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "start", nullable = false)
    private LocalDateTime start = LocalDateTime.now();

    @Column(name = "end")
    private LocalDateTime end;

    @Column(name = "is_ended", nullable = false)
    private Boolean isEnded = false;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;
    //private String thumbnail = "/path/to/default/thumbnail";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "audio_feature_id")
    private AudioFeature audioFeature;

    @OneToOne
    @JoinColumn(name = "dj_id")
    private Dj dj;

    @OneToMany(mappedBy = "channel")
    private List<Track> tracks = new ArrayList<>();

    public void changeDj(Dj dj) {
        this.dj = dj;
    }

    public void changeAudioFeature(AudioFeature audioFeature) {
        this.audioFeature = audioFeature;
    }

    public void changeUser(User user) {
        if (this.user != null) {
            this.user.getChannels().remove(this);
        }
        this.user = user;
        user.getChannels().add(this);
    }

    public void changeThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
