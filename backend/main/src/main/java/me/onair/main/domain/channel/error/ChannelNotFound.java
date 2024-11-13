package me.onair.main.domain.channel.error;


import me.onair.main.global.error.CustomException;
import me.onair.main.global.error.ErrorCode;

public class ChannelNotFound extends CustomException {

  public ChannelNotFound(ErrorCode errorCode) {
    super(errorCode);
  }
}
