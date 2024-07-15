package io.deeplay.camp.entities;

public class Healer extends Unit {
  public Healer() {
    setMaxHp(10);
    setNowHp(10);
    setDamage(5);
    setAccuracy(5);
    setArmor(12);
    setGeneral(false);
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
