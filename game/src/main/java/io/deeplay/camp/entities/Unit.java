package io.deeplay.camp.entities;

public abstract class Unit {
  protected int maxHp;
  protected int nowHp;
  protected int damage;
  protected int accuracy;
  protected int armor;
  protected boolean isGeneral = false;

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

  public boolean isAlive() {
    return nowHp > 0;
  }

  public boolean isGeneral() {
    return isGeneral;
  }

  // Методы для реализации в дочерних классах
  public abstract void playMove(Unit targetUnit);
}
