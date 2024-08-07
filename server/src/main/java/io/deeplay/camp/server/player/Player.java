package io.deeplay.camp.server.player;

import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Player {
  protected PlayerType playerType;

  public Player() {}

  public Player(PlayerType playerType) {
    this.playerType = playerType;
  }

  public abstract void updateGameState(GameStateDto gameStateDto);

  public abstract boolean isBotPlayer();
}
