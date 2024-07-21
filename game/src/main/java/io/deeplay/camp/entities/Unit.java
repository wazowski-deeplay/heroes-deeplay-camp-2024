package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;

public abstract class Unit implements GeneralBuff {
  protected int maxHp;
  protected int nowHp;
  protected int damage;
  protected int accuracy;
  protected int armor;
  protected boolean isGeneral = false;
  protected UnitType unitType;
  protected PlayerType playerType;
  protected AttackType attackType;

  public Unit(
      UnitType unitType,
      int maxHp,
      int nowHp,
      int damage,
      int accuracy,
      int armor,
      boolean isGeneral) {
    this.unitType = unitType;
    this.maxHp = maxHp;
    this.nowHp = nowHp;
    this.damage = damage;
    this.accuracy = accuracy;
    this.armor = armor;
    this.isGeneral = isGeneral;
  }

  public void setAttack(AttackType attackType) {
    this.attackType = attackType;
  }

  // Геттеры
  public int getMaxHp() {
    return maxHp;
  }

  // Сеттеры
  protected void setMaxHp(int health) {
    this.maxHp = health;
  }

  public int getNowHp() {
    return nowHp;
  }

  public void setNowHp(int health) {
    this.nowHp = health;
    if (this.nowHp < 0) {
      this.nowHp = 0;
    }
    if (this.nowHp > maxHp) {
      this.nowHp = maxHp;
    }
  }

  public int getDamage() {
    return damage;
  }

  protected void setDamage(int damage) {
    this.damage = damage;
  }

  public int getAccuracy() {
    return accuracy;
  }

  protected void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

  public int getArmor() {
    return armor;
  }

  protected void setArmor(int armor) {
    this.armor = armor;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public PlayerType getPlayerType() {
    return playerType;
  }

  public void setPlayerType(PlayerType playerType) {
    this.playerType = playerType;
  }

  public AttackType getAttackType() {
    return attackType;
  }

  public boolean isAlive() {
    return nowHp > 0;
  }

  public boolean isGeneral() {
    return isGeneral;
  }

  public void setGeneral(boolean isGeneral) {
    this.isGeneral = isGeneral;
  }

  // Методы для реализации в дочерних классах
  public abstract void playMove(Unit targetUnit);
}
