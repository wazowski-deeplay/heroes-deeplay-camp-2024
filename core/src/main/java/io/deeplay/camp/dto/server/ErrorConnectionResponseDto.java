package io.deeplay.camp.dto.server;

import io.deeplay.camp.exceptions.ErrorCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorConnectionResponseDto extends ServerDto {
  private ConnectionErrorCode connectionErrorCode;
  private String message;

  public ErrorConnectionResponseDto(ConnectionErrorCode connectionErrorCode, String message) {
    super(ServerDtoType.ERROR_CONNECTION_INFO);
    this.connectionErrorCode = connectionErrorCode;
    this.message = message;
  }

  public ConnectionErrorCode getConnectionErrorCode() {
    return connectionErrorCode;
  }


  public String getMessage() {
    return message;
  }
}
