package me.onair.main.domain.channel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest.CreateNewChannelTrackRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "artist", nullable = false, length = 100)
    private String artist;

    @Column(name = "cover", nullable = false, columnDefinition = "TEXT")
    private String cover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public void setChannel(Channel channel) {
        if (this.channel != null) {
            this.channel.getTracks().remove(this);
        }
        this.channel = channel;
        channel.getTracks().add(this);
    }

    @Builder
    private Track(String title, String artist, String cover) {
        this.title = title;
        this.artist = artist;
        this.cover = cover;
    }

    public static Track createTrack(CreateNewChannelTrackRequest request, Channel channel) {
        Track track = Track.builder()
                .title(request.getTitle())
                .artist(request.getArtist())
                .build();
        track.setChannel(channel);
        return track;
    }

    public static List<Track> createTrackList(List<CreateNewChannelTrackRequest> trackListRequest, Channel channel) {
        List<Track> trackList = new ArrayList<>();
        for (CreateNewChannelTrackRequest track : trackListRequest) {
            trackList.add(createTrack(track, channel));
        }
        return trackList;
    }
}
