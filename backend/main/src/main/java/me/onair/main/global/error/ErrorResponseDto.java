package me.onair.main.global.error;

import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final String message;
    private final String code;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }
}