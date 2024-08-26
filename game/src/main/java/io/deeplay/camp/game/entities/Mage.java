package io.deeplay.camp.game.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Mage extends Unit {
  public Mage(PlayerType playerType) {
    super(UnitType.MAGE, 10, 10, 2, 2, 10, false);
    this.playerType = playerType;
    setAttack(AttackType.MASS_ATTACK);
  }

  public Mage(Mage mage) {
    super(mage);
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
  @JsonIgnore

  @Override
  public Unit getCopy() {
    return new Mage(this);
  }
}
