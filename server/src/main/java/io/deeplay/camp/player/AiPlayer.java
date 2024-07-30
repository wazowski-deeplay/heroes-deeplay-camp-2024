package io.deeplay.camp.player;

import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.mechanics.PlayerType;

public class AiPlayer extends Player {

  public AiPlayer(PlayerType playerType) {
    super(playerType);
  }

  @Override
  public void updateGameState(GameStateDto gameStateDto) {}
}
