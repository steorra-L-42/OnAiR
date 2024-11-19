package me.onair.main.domain.user.error;

public class DuplicatePhoneNumberException extends IllegalArgumentException{
    public DuplicatePhoneNumberException() {
        super("이미 가입된 전화번호입니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}