package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Mage extends Unit {
  public Mage(PlayerType playerType) {
    super(UnitType.MAGE, 10, 10,5,5,12,false);
    this.playerType = playerType;
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
