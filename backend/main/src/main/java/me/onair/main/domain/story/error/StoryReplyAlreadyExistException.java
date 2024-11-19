package me.onair.main.domain.story.error;

import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class StoryReplyAlreadyExistException extends CustomException {

    public StoryReplyAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}