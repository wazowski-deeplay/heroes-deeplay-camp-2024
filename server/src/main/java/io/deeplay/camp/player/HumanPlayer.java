package io.deeplay.camp.player;

import io.deeplay.camp.mechanics.PlayerType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HumanPlayer extends Player {
  private UUID clientId;

  public HumanPlayer(PlayerType playerType, UUID humanPlayerId) {
    super(playerType);
    this.clientId = humanPlayerId;
  }

  public HumanPlayer(UUID clientId) {
    this.clientId = clientId;
  }

  public HumanPlayer(PlayerType playerType) {
    super(playerType);
  }
}
