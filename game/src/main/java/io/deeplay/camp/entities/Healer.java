package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Healer extends Unit {
  public Healer(PlayerType playerType) {
    super(UnitType.HEALER, 10, 10,5,15,12,false);
    this.playerType = playerType;
  }

  @Override
  public void playMove(Unit targetUnit) {
    targetUnit.setNowHp(targetUnit.getNowHp() + damage);
  }
}
