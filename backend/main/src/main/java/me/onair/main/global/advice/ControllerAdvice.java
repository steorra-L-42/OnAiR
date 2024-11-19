package me.onair.main.global.advice;

import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.jwt.error.ExpiredRefreshTokenException;
import me.onair.main.domain.jwt.error.NoRefreshTokenCookieException;
import me.onair.main.domain.jwt.error.NotExistRefreshTokenException;
import me.onair.main.domain.jwt.error.WrongCategoryJwtException;
import me.onair.main.domain.user.error.DuplicatePhoneNumberException;
import me.onair.main.domain.user.error.DuplicateUsername;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.error.NotVerifiedPhoneNumberException;
import me.onair.main.domain.user.error.SMSException;
import me.onair.main.domain.user.error.UsernameTooShortOrLongException;
import me.onair.main.domain.user.error.VerificationCodeRequestExceedLimitException;
import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;
import me.onair.main.global.error.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(DuplicateUsername.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateUsername(DuplicateUsername e) {
        log.error("handleDuplicateUsername", e);
        return getResponse(ErrorCode.DUPLICATE_USERNAME);
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleExpiredRefreshTokenException(ExpiredRefreshTokenException e) {
        log.error("handleExpiredRefreshTokenException : Refresh Token Expired");
        return getResponse(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    @ExceptionHandler(WrongCategoryJwtException.class)
    public ResponseEntity<ErrorResponseDto> handleWrongCategoryJwtException(WrongCategoryJwtException e) {
        log.error("handleWrongCategoryJwtException", e);
        return getResponse(ErrorCode.WRONG_CATEGORY_JWT);
    }

    @ExceptionHandler(NotExistUserException.class)
    public ResponseEntity<ErrorResponseDto> handleNotExistUserException(NotExistUserException e) {
        log.error("handleNotExistUserException", e);
        return getResponse(ErrorCode.NOT_EXIST_USER);
    }

    @ExceptionHandler(NotExistRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleNotExistRefreshTokenException(NotExistRefreshTokenException e) {
        log.error("handleNotExistRefreshTokenException", e);
        return getResponse(ErrorCode.NOT_EXIST_REFRESH_TOKEN);
    }

    @ExceptionHandler(NoRefreshTokenCookieException.class)
    public ResponseEntity<ErrorResponseDto> handleNoRefreshTokenCookieException(NoRefreshTokenCookieException e) {
        log.error("handleNoRefreshTokenCookieException", e);
        return getResponse(ErrorCode.NO_REFRESH_TOKEN_COOKIE);
    }

    @ExceptionHandler(VerificationCodeRequestExceedLimitException.class)
    public ResponseEntity<ErrorResponseDto> handleVerificationCodeRequestExceedLimitException(
            VerificationCodeRequestExceedLimitException e) {
        log.error("handleVerificationCodeRequestExceedLimitException", e);
        return getResponse(ErrorCode.VERIFICATION_CODE_REQUEST_EXCEED_LIMIT);
    }

    @ExceptionHandler(DuplicatePhoneNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicatePhoneNumberException(DuplicatePhoneNumberException e) {
        log.error("handleDuplicatePhoneNumberException", e);
        return getResponse(ErrorCode.DUPLICATE_PHONE_NUMBER);
    }

    @ExceptionHandler(SMSException.class)
    public ResponseEntity<ErrorResponseDto> handleSMSException(SMSException e) {
        log.error("handleSMSException", e);
        return getResponse(ErrorCode.SMS_EXCEPTION);
    }

    @ExceptionHandler(UsernameTooShortOrLongException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameTooLongException(UsernameTooShortOrLongException e) {
        log.error("handleUsernameTooLongException", e);
        return getResponse(ErrorCode.USERNAME_TOO_SHORT_OR_LONG);
    }

    @ExceptionHandler(NotVerifiedPhoneNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleNotVerifiedPhoneNumberException(NotVerifiedPhoneNumberException e) {
        log.error("handleNotVerifiedPhoneNumberException", e);
        return getResponse(ErrorCode.NOT_VERIFIED_PHONE_NUMBER);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("handleMaxUploadSizeExceededException", e);
        return getResponse(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e) {
        log.error(e.getErrorCode().getMessage());
        return getResponse(e.getErrorCode());
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
