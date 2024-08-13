package io.deeplay.camp.game.entities;

import lombok.Getter;

public interface GeneralBuff {
  default void applyBuff(Unit unit, UnitType typeBuff) {
    switch (typeBuff) {
      case KNIGHT -> unit.setArmor(unit.getArmor() + Buffs.ARMOR.getValue());
      case MAGE -> unit.setDamage(unit.getDamage() + Buffs.DAMAGE.getValue());
      case ARCHER -> unit.setAccuracy(unit.getAccuracy() + Buffs.ACCURACY.getValue());
      case HEALER -> {
        unit.setMaxHp(unit.getMaxHp() + Buffs.MAXHP.getValue());
        unit.setCurrentHp(unit.getMaxHp() + Buffs.MAXHP.getValue());
      }
      default -> unit.setCurrentHp(unit.currentHp);
    }
  }

  default void removeBuff(Unit unit, UnitType typeBuff) {
    switch (typeBuff) {
      case KNIGHT -> unit.setArmor(unit.getArmor() - Buffs.ARMOR.getValue());
      case MAGE -> unit.setDamage(unit.getDamage() - Buffs.DAMAGE.getValue());
      case ARCHER -> unit.setAccuracy(unit.getAccuracy() - Buffs.ACCURACY.getValue());
      case HEALER -> {
        unit.setMaxHp(unit.getMaxHp() - Buffs.MAXHP.getValue());
        if (unit.getCurrentHp() > unit.getMaxHp()) {
          unit.setCurrentHp(unit.getMaxHp());
        }
      }
      default -> unit.setCurrentHp(unit.currentHp);
    }
  }

  @Getter
  enum Buffs {
    ARMOR(4),
    DAMAGE(1),
    ACCURACY(4),
    MAXHP(6);
    final int value;

    Buffs(int value) {
      this.value = value;
    }
  }
}
