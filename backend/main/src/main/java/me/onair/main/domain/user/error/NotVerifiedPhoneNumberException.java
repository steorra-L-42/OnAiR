package me.onair.main.domain.user.error;

public class NotVerifiedPhoneNumberException extends RuntimeException {

    public NotVerifiedPhoneNumberException() {
        super("휴대폰 인증이 완료되지 않은 사용자입니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
