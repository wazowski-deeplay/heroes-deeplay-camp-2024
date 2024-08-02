package io.deeplay.camp.dto.server;

import io.deeplay.camp.exceptions.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorGameResponseDto extends ServerDto {
  private ErrorCode errorCode;
  private String message;

  public ErrorGameResponseDto(ErrorCode errorCode, String message) {
    super(ServerDtoType.ERROR_GAME_INFO);
    this.errorCode = errorCode;
    this.message = message;
  }
}
