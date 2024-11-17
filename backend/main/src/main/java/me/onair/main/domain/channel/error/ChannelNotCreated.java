package me.onair.main.domain.channel.error;

import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class ChannelNotCreated extends CustomException {
    public ChannelNotCreated(ErrorCode errorCode) {
        super(errorCode);
    }
}
