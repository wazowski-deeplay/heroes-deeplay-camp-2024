package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Archer extends Unit {
  public Archer(PlayerType playerType) {
    super(UnitType.ARCHER, 10, 10, 5, 7, 12, false);
    this.playerType = playerType;
    setAttack(AttackType.LONG_ATTACK);
  }

  public Archer(Archer archer) {
    super(archer);
  }

  @Override
  public void playMove(Unit targetUnit) {
    int diceRoll = (int) (Math.random() * 20);
    if (diceRoll + accuracy > targetUnit.getArmor()) {
      targetUnit.setCurrentHp(targetUnit.getCurrentHp() - damage);
      hitTarget = true;
    } else {
      hitTarget = false;
    }
    isMoved = true;
  }
}
