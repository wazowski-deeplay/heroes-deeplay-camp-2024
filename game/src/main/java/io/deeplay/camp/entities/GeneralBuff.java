package io.deeplay.camp.entities;

public interface GeneralBuff {
  enum Buffs {
    ARMOR(3),
    DAMAGE(3),
    ACCURACY(3);
    final int value;

    Buffs(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  default void applyBuff(Unit unit, UnitType typeBuff) {
    switch (typeBuff) {
      case KNIGHT:
        unit.setArmor(unit.getArmor() + Buffs.ARMOR.getValue());
        break;
      case MAGE:
        unit.setDamage(unit.getDamage() + Buffs.DAMAGE.getValue());
        break;
      case ARCHER:
        unit.setAccuracy(unit.getAccuracy() + Buffs.ACCURACY.getValue());
        break;
      default:
        break;
    }
  }

  default void removeBuff(Unit unit, UnitType typeBuff) {
    switch (typeBuff) {
      case KNIGHT:
        unit.setArmor(unit.getArmor() - Buffs.ARMOR.getValue());
        break;
      case MAGE:
        unit.setDamage(unit.getDamage() - Buffs.DAMAGE.getValue());
        break;
      case ARCHER:
        unit.setAccuracy(unit.getAccuracy() - Buffs.ACCURACY.getValue());
        break;
      default:
        break;
    }
  }
}
