package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Healer extends Unit {
  public Healer(PlayerType playerType) {
    super(UnitType.HEALER, 10, 10, 5, 15, 12, false);
    this.playerType = playerType;
    setAttack(AttackType.LONG_ATTACK);
  }

  @Override
  public void playMove(Unit targetUnit) {
    targetUnit.setCurrentHp(targetUnit.getCurrentHp() + damage);
    isMoved = true;
  }
}
