package io.deeplay.camp.core.dto.server;

import io.deeplay.camp.game.mechanics.PlayerType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GamePartyInfoDto extends ServerDto {
  private UUID gamePartyId;
  private PlayerType playerType;

  public GamePartyInfoDto(UUID gamePartyId, PlayerType playerType) {
    super(ServerDtoType.GAME_PARTY_INFO);
    this.gamePartyId = gamePartyId;
    this.playerType = playerType;
  }
}
