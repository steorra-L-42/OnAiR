package me.onair.main.domain.s3.error;

import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class ImageArrayNullException extends CustomException {

    public ImageArrayNullException(ErrorCode errorCode) {
        super(errorCode);
    }
}