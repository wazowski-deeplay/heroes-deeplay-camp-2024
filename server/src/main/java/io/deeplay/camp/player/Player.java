package io.deeplay.camp.player;

import io.deeplay.camp.mechanics.PlayerType;
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
}
