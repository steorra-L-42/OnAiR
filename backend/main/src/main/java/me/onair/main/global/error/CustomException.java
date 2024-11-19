package me.onair.main.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public abstract class CustomException extends RuntimeException{

  private ErrorCode errorCode;

  public CustomException() {
    this.errorCode = ErrorCode.CUSTOM_EXCEPTION;
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
