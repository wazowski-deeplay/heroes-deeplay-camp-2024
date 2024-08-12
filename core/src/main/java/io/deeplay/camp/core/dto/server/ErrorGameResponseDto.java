package io.deeplay.camp.core.dto.server;

import io.deeplay.camp.game.exceptions.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ErrorGameResponseDto extends ServerDto {
  private ErrorCode errorCode;
  private String message;
  private UUID gamePartyId;

  public ErrorGameResponseDto(ErrorCode errorCode, String message, UUID gamePartyId) {
    super(ServerDtoType.ERROR_GAME_INFO);
    this.errorCode = errorCode;
    this.message = message;
    this.gamePartyId = gamePartyId;
  }
}
