package io.deeplay.camp.exceptions;

import lombok.Getter;

@Getter
public class GameException extends Exception {
  public ErrorCode errorCode;

  public GameException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
