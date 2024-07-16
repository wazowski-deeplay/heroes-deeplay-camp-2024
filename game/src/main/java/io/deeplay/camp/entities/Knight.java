package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Knight extends Unit {
  public Knight(PlayerType playerType) {
    super(UnitType.KNIGHT, 15, 15,7,4,15,false);
    this.playerType = playerType;
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
