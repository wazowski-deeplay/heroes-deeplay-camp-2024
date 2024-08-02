package io.deeplay.camp.dto.server;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorConnectionResponseDto extends ServerDto {
  private ConnectionErrorCode connectionErrorCode;
  private String message;

  public ErrorConnectionResponseDto(ConnectionErrorCode connectionErrorCode, String message) {
    super(ServerDtoType.ERROR_CONNECTION_INFO);
    this.connectionErrorCode = connectionErrorCode;
    this.message = message;
  }
}
