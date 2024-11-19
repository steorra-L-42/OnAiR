package me.onair.main.domain.user.error;

public class SMSException extends RuntimeException {
    public SMSException() {
        super("SMS 발송에 실패했습니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
