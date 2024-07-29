package io.deeplay.camp.dto.server;

import java.util.UUID;
import lombok.Getter;

@Getter
public class GamePartyInfoDto extends ServerDto {
  private UUID gamePartyId;
  public GamePartyInfoDto(UUID gamePartyId) {
    this.gamePartyId = gamePartyId;
  }
}
