package me.onair.main.util;

import static me.onair.main.domain.channel.enums.NewsTopic.LIFE_CULTURE;
import static me.onair.main.domain.channel.enums.Personality.SEXY;
import static me.onair.main.domain.channel.enums.TtsEngine.TYPECAST_SENA;

import java.util.List;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.user.dto.SignupRequest;

public class RequestUtil {
    // 기본적인 SignupRequest 객체를 반환하는 메소드
    public static SignupRequest createSignupRequest() {
        return new SignupRequest(
                "bbamjoong",            // username
                "hotfriedPWPW404",      // password
                "hotfriedBbamjoong",    // nickname
                "01099999999",          // phoneNumber
                "123456"                // verification
        );
    }

    // 기본적인 ChannelCreateRequest 객체를 반환하는 메소드
    public static CreateNewChannelRequest createNewChannelRequest() {
        // CreateNewChannelTrackRequest 객체 생성 (곡 정보 추가)
        CreateNewChannelRequest.CreateNewChannelTrackRequest track =
                new CreateNewChannelRequest.CreateNewChannelTrackRequest(
                        "Sneakers",  // title
                        "ITZY", // artist
                        "cover_image.jpg"  // cover
                );

        // CreateNewChannelRequest 객체 생성
        return new CreateNewChannelRequest(
                TYPECAST_SENA,  // ttsEngine
                SEXY,            // personality
                LIFE_CULTURE,    // newsTopic
                "https://www.example.com",  // thumbnail
                "YTN",           // channelName
                List.of(track)   // trackList에 곡 하나를 추가
        );
    }
}
