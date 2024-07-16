package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Archer extends Unit {
  public Archer(PlayerType playerType) {
    super(UnitType.ARCHER, 10, 10,5,6,12,false);
    this.playerType = playerType;
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
