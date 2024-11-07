package me.onair.main.domain.jwt.error;

import io.jsonwebtoken.ExpiredJwtException;

public class ExpiredRefreshTokenException extends ExpiredJwtException {

    public ExpiredRefreshTokenException(ExpiredJwtException e){
        super(e.getHeader(), e.getClaims(), e.getMessage(), e);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
