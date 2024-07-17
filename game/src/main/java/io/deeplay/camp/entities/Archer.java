package io.deeplay.camp.entities;

public class Archer extends Unit {
  public Archer() {
    setMaxHp(10);
    setNowHp(10);
    setDamage(5);
    setAccuracy(6);
    setArmor(12);
    setGeneral(false);
    setAttack(AttackType.LONG_ATTACK);
    setPlayerType(this.playerType);
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
