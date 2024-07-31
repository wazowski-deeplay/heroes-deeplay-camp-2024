package io.deeplay.camp.dto.server;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ErrorResponseDto extends ServerDto {
  private ServerDtoType serverDtoType;
  private ConnectionErrorCode errorCode;
  private String message;

  public ErrorResponseDto(ConnectionErrorCode errorCode, String message) {
    this.serverDtoType = ServerDtoType.ERROR_INFO;
    this.errorCode = errorCode;
    this.message = message;
  }

  public ConnectionErrorCode getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}
