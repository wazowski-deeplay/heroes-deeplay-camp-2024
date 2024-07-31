package io.deeplay.camp.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GamePartyInfoDto extends ServerDto {
  private UUID gamePartyId;

  public GamePartyInfoDto(UUID gamePartyId) {
    super(ServerDtoType.GAME_PARTY_INFO);
    this.gamePartyId = gamePartyId;
  }
}
