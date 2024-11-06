package me.onair.main.domain.user.error;

public class DuplicateUsername extends IllegalArgumentException{
    public DuplicateUsername(String username) {
        super("Username is already exist: " + username);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}