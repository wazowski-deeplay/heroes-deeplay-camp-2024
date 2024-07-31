package io.deeplay.camp.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "unitType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Knight.class, name = "KNIGHT"),
        @JsonSubTypes.Type(value = Mage.class, name = "MAGE"),
        @JsonSubTypes.Type(value = Healer.class, name = "HEALER"),
        @JsonSubTypes.Type(value = Archer.class, name = "ARCHER"),
})
public abstract class Unit implements GeneralBuff {
  protected int maxHp;
  protected int currentHp;
  protected int damage;
  protected int accuracy;
  protected int armor;
  protected boolean isGeneral;
  protected UnitType unitType;
  protected PlayerType playerType;
  protected AttackType attackType;
  // Поле для проверки, походил ли данный юнит в этом ходу или нет
  protected boolean isMoved = false;

  public Unit(
      UnitType unitType,
      int maxHp,
      int currentHp,
      int damage,
      int accuracy,
      int armor,
      boolean isGeneral) {
    this.unitType = unitType;
    this.maxHp = maxHp;
    this.currentHp = currentHp;
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

  public int getCurrentHp() {
    return currentHp;
  }

  public void setCurrentHp(int health) {
    this.currentHp = health;
    if (this.currentHp < 0) {
      this.currentHp = 0;
    }
    if (this.currentHp > maxHp) {
      this.currentHp = maxHp;
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

  public void setMoved(boolean isMoved) {
    this.isMoved = isMoved;
  }

  public boolean getMoved() {
    return isMoved;
  }

  public void setPlayerType(PlayerType playerType) {
    this.playerType = playerType;
  }

  public AttackType getAttackType() {
    return attackType;
  }

  public boolean isAlive() {
    return currentHp > 0;
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
