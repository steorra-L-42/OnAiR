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

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "Duplicate username", "B001"),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "Duplicate phone number", "B002"),
    NOT_VERIFIED_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "Not verified phone number", "B003"),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "Not exist user", "B004"),
    VERIFICATION_CODE_REQUEST_EXCEED_LIMIT(HttpStatus.BAD_REQUEST, "Verification code request exceed limit", "B005"),
    USERNAME_TOO_SHORT_OR_LONG(HttpStatus.BAD_REQUEST, "Username too short or long", "B006"),

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access token is expired", "C001"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token is expired", "C002"),
    WRONG_CATEGORY_JWT(HttpStatus.UNAUTHORIZED, "Wrong Category JWT", "C003"),
    NOT_EXIST_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Not exist refresh token", "C004"),
    NO_REFRESH_TOKEN_COOKIE(HttpStatus.UNAUTHORIZED, "No refresh token cookie", "C005"),

    SMS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "SMS Exception", "D001"),

    //채널
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel Not Found", "E001"),
    ENDED_CHANNEL(HttpStatus.BAD_REQUEST, "Ended channel", "E002"),
    CHANNEL_MISMATCH(HttpStatus.BAD_REQUEST, "Channel Mismatch", "E003"),
    CHANNEL_NOT_CREATED(HttpStatus.BAD_REQUEST, "Channel Not Created", "E004"),

    // 사연
    STORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Story Not Found", "F001"),
    STORY_REPLY_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Story Reply Already Exist", "F002"),

    // S3
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image", "G001"),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "Inavlid File Format", "G002"),
    IMAGE_ARRAY_NULL(HttpStatus.BAD_REQUEST, "Image arary is null", "G003"),
    MAX_UPLOAD_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "Max Upload Size Exceeded", "G004"),
    MULTIPARTFILE_IS_NULL(HttpStatus.BAD_REQUEST, "Multipartfile is null", "G005"),

    //기본 에러
    CUSTOM_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Custom Exception", "Z001");


    private final HttpStatus status;
    private final String message;
    private final String code;
}
