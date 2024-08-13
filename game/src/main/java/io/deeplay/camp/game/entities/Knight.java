package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Knight extends Unit {
  public Knight(PlayerType playerType) {
    super(UnitType.KNIGHT, 15, 15, 5, 5, 15, false);
    this.playerType = playerType;
    setAttack(AttackType.CLOSE_ATTACK);
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
