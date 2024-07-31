package io.deeplay.camp.exceptions;

import io.deeplay.camp.dto.server.ConnectionErrorCode;
import lombok.Getter;

@Getter
public class GameManagerException extends Exception {
  public ConnectionErrorCode connectionErrorCode;

  public GameManagerException(ConnectionErrorCode connectionErrorCode) {
    super(connectionErrorCode.getMessage());
    this.connectionErrorCode = connectionErrorCode;
  }
}
