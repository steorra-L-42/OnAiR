package me.onair.main.domain.channel.dto;


import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.entity.Channel;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateNewChannelResponse {

    private String channelUuid;
    private LocalDateTime start;
    private LocalDateTime end;
    private Boolean isDefault;

    public static CreateNewChannelResponse from(Channel channel) {
        return new CreateNewChannelResponse(channel.getUuid(), channel.getStart(), channel.getEnd(), channel.getIsDefault());
    }
}
