package me.onair.main.domain.user.error;

public class VerificationCodeRequestExceedLimitException extends RuntimeException {

    public VerificationCodeRequestExceedLimitException() {
        super("인증번호 요청 횟수를 초과하였습니다.");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
