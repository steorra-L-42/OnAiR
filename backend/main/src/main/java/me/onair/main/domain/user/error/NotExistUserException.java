package me.onair.main.domain.user.error;

public class NotExistUserException extends RuntimeException {

    public NotExistUserException() {
        super("존재하지 않는 사용자입니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}