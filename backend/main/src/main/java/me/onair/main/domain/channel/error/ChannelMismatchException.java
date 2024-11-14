package me.onair.main.domain.channel.error;


import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class ChannelMismatchException extends CustomException {

    public ChannelMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
