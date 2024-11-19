package me.onair.main.domain.jwt.error;

public class NotExistRefreshTokenException extends RuntimeException {
    public NotExistRefreshTokenException() {
        super("존재하지 않는 Refresh Token입니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
