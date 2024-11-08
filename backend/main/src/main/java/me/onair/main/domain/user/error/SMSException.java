package me.onair.main.domain.user.error;

import org.springframework.http.HttpHeaders;

public class SMSException extends RuntimeException {
    public SMSException() {
        super("SMS 발송에 실패했습니다.");
    }

    public SMSException(HttpHeaders headers) {
        super("SMS 발송에 실패했습니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
