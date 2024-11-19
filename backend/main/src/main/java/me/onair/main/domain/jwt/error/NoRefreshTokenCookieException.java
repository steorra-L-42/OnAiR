package me.onair.main.domain.jwt.error;

public class NoRefreshTokenCookieException extends RuntimeException {

    public NoRefreshTokenCookieException() {
        super("No Refresh Token Cookie");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
