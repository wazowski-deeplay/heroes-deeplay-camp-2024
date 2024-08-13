package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Healer extends Unit {
  public Healer(PlayerType playerType) {
    super(UnitType.HEALER, 11, 11, 4, 16, 12, false);
    this.playerType = playerType;
    setAttack(AttackType.LONG_ATTACK);
  }

  public Healer(Healer healer) {
    super(healer);
  }

  @Override
  public void playMove(Unit targetUnit) {
    targetUnit.setCurrentHp(targetUnit.getCurrentHp() + damage);
    hitTarget = true;
    isMoved = true;
  }
}
