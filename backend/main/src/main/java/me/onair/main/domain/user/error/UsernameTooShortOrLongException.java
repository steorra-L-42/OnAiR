package me.onair.main.domain.user.error;

public class UsernameTooShortOrLongException extends IllegalArgumentException {

    public UsernameTooShortOrLongException() {
        super("Username is too short or long");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
