package io.deeplay.camp.entities;

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

  enum Buffs {
    ARMOR(3),
    DAMAGE(1),
    ACCURACY(3),
    MAXHP(2);
    final int value;

    Buffs(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }
}
