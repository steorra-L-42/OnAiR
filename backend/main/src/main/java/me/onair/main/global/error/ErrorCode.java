package me.onair.main.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ENUM_NAME(HttpStatus.OK, "message");

    private final HttpStatus status;
    private final String message;
}
