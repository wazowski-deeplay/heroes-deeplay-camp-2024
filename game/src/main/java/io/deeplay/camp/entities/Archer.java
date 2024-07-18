package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public class Archer extends Unit {
  public Archer(PlayerType playerType) {
    super(UnitType.ARCHER, 10, 10, 5, 6, 12, false);
    this.playerType = playerType;
    setAttack(AttackType.LONG_ATTACK);
  }

  @Override
  public void playMove(Unit targetUnit) {
    int diceRoll = (int) (Math.random() * 20);
    if (diceRoll + accuracy > targetUnit.getArmor()) {
      targetUnit.setNowHp(targetUnit.getNowHp() - damage);
    }
  }
}
