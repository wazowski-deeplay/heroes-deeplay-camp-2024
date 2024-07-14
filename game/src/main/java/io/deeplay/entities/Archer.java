package io.deeplay.entities;

public class Archer extends Unit {
  public Archer() {
    setMaxHP(10);
    setNowHP(10);
    setDamage(5);
    setAccuracy(6);
    setArmor(12);
    setGeneral(false);
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
