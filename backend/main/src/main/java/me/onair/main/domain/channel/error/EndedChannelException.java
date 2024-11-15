package me.onair.main.domain.channel.error;

import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class EndedChannelException extends CustomException {

    public EndedChannelException(ErrorCode errorCode) {
        super(errorCode);
    }
}