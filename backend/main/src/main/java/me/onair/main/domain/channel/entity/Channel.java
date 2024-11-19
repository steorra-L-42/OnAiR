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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "channel")
@ToString
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "channel_name", nullable = false)
    private String channelName;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "dj_id")
    private Dj dj;

    @OneToMany(mappedBy = "channel")
    private List<Track> tracks = new ArrayList<>();

    @OneToMany(mappedBy = "channel")
    private List<Story> stories = new ArrayList<>();

    @Builder
    private Channel(String uuid, String channelName, Boolean isDefault, String thumbnail,
                    LocalDateTime start, LocalDateTime end, Boolean isEnded) {
        this.uuid = uuid;
        this.channelName = channelName;
        this.isDefault = isDefault;
        this.thumbnail = thumbnail;
        this.start = start;
        this.end = end;
        this.isEnded = isEnded;
    }

    public static Channel createChannel(CreateNewChannelRequest request, User user) {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
        String uuidString = Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);

        boolean isDefault = (user.getRole() == Role.ROLE_ADMIN);
        LocalDateTime end = isDefault ? LocalDateTime.now().plusDays(1) : LocalDateTime.now().plusHours(2);

        Channel channel = Channel.builder()
                .uuid(uuidString)
                .channelName(request.getChannelName())
                .isDefault(isDefault)
                .thumbnail(request.getThumbnail())
                .start(LocalDateTime.now())
                .end(end)
                .isEnded(false)
                .build();
        channel.changeUser(user);
        return channel;
    }

    public void changeDj(Dj dj) {
        this.dj = dj;
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

    public void endChannel() {
        this.isEnded = true;
    }
}
