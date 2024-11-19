package me.onair.main.domain.channel.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.onair.main.domain.channel.entity.Channel;

@Getter
@ToString
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelListResponse {

  List<ChannelInfoResponse> channelList;

  public static ChannelListResponse from(List<Channel> channelList) {
    List<ChannelInfoResponse> channelInfoResponseList = new ArrayList<>();
    for (Channel channel : channelList) {
      channelInfoResponseList.add(ChannelInfoResponse.from(channel));
    }
    return new ChannelListResponse(channelInfoResponseList);
  }
}
