package me.onair.main.domain.jwt.error;

public class WrongCategoryJwtException extends RuntimeException {
    public WrongCategoryJwtException() {
        super("Wrong Category JWT");
    }
}
