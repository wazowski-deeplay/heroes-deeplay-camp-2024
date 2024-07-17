package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public abstract class Unit {
  protected int maxHp;
  protected int nowHp;
  protected int damage;
  protected int accuracy;
  protected int armor;
  protected boolean isGeneral = false;
  protected AttackType attackType;
  protected PlayerType playerType;

  // Сеттеры
  protected void setMaxHp(int health) {
    this.maxHp = health;
  }

  protected void setNowHp(int health) {
    this.nowHp = health;
    if (this.nowHp < 0) {
      this.nowHp = 0;
    }
    if (this.nowHp > maxHp) {
      this.nowHp = maxHp;
    }
  }

  protected void setDamage(int damage) {
    this.damage = damage;
  }

  protected void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

  protected void setArmor(int armor) {
    this.armor = armor;
  }

  public void setGeneral(boolean isGeneral) {
    this.isGeneral = isGeneral;
  }

  public void setAttack(AttackType attackType) {
    this.attackType = attackType;
  }

  public void setPlayerType(PlayerType playerType) {
    this.playerType = playerType;
  }

  // Геттеры
  public int getMaxHp() {
    return maxHp;
  }

  public int getNowHp() {
    return nowHp;
  }

  public int getDamage() {
    return damage;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public int getArmor() {
    return armor;
  }

  public AttackType getAttackType() {
    return attackType;
  }

  public PlayerType getPlayerType() {
    return playerType;
  }

  public boolean isAlive() {
    return nowHp > 0;
  }

  public boolean isGeneral() {
    return isGeneral;
  }

  // method for implemetation in child class
  public abstract void playMove(Unit targetUnit);
}
