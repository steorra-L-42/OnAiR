package me.onair.main.domain.channel.error;


import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class ChannelNotFoundException extends CustomException {

    public ChannelNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
