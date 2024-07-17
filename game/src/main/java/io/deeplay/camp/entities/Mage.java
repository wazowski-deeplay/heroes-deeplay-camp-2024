package io.deeplay.camp.entities;

public class Mage extends Unit {
  public Mage() {
    setMaxHp(10);
    setNowHp(10);
    setDamage(5);
    setAccuracy(5);
    setArmor(12);
    setGeneral(false);
    setAttack(AttackType.MASS_ATTACK);
    setPlayerType(this.playerType);
  }

  @Override
  public void playMove(Unit targetUnit) {}
}
