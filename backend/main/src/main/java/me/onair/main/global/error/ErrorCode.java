package me.onair.main.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 일단 대충 비슷한 것들끼리 묶음
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request", "A001"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized", "A002"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden", "A003"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found", "A004"),

    DUPLICATE_USERNAME(HttpStatus.CONFLICT,"Duplicate username", "B001"),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "Duplicate phone number", "B002"),
    NOT_VERIFIED_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "Not verified phone number", "B003"),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "Not exist user", "B004"),

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access token is expired", "C001"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token is expired", "C002"),
    WRONG_CATEGORY_JWT(HttpStatus.UNAUTHORIZED, "Wrong Category JWT", "C003"),
    NOT_EXIST_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Not exist refresh token", "C004"),
    NO_REFRESH_TOKEN_COOKIE(HttpStatus.UNAUTHORIZED, "No refresh token cookie", "C005");

    private final HttpStatus status;
    private final String message;
    private final String code;
}
