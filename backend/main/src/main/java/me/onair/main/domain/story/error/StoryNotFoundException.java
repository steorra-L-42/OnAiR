package me.onair.main.domain.story.error;

import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class StoryNotFoundException extends CustomException {

    public StoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}