package io.deeplay.camp.entities;

public enum UnitType {
  KNIGHT,
  ARCHER,
  MAGE,
  HEALER;

  public static UnitType getRandom() {
    return values()[(int) (Math.random() * values().length)];
  }
}
