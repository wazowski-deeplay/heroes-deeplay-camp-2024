package io.deeplay.camp.game.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "unitType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Knight.class, name = "KNIGHT"),
  @JsonSubTypes.Type(value = Mage.class, name = "MAGE"),
  @JsonSubTypes.Type(value = Healer.class, name = "HEALER"),
  @JsonSubTypes.Type(value = Archer.class, name = "ARCHER"),
})
public abstract class Unit implements GeneralBuff {
  // Геттеры
  @Getter protected int maxHp;
  @Getter protected int currentHp;
  @Getter protected int damage;
  @Getter protected int accuracy;
  @Getter protected int armor;
  protected boolean isGeneral;
  @Getter protected UnitType unitType;
  @Setter @Getter protected PlayerType playerType;
  @Getter protected AttackType attackType;
  // Поле для проверки, походил ли данный юнит в этом ходу или нет
  @Getter protected boolean isMoved = false;
  @Getter boolean hitTarget = false;

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

  public Unit(Unit unit) {
    this.maxHp = unit.getMaxHp();
    this.currentHp = unit.getCurrentHp();
    this.damage = unit.getDamage();
    this.accuracy = unit.getAccuracy();
    this.armor = unit.getArmor();
    this.isGeneral = unit.isGeneral;
    this.unitType = unit.getUnitType();
    this.playerType = unit.getPlayerType();
    this.isMoved = unit.isMoved;
    this.hitTarget = unit.hitTarget;
    this.attackType = unit.getAttackType();
  }

  public void setAttack(AttackType attackType) {
    this.attackType = attackType;
  }

  // Сеттеры
  protected void setMaxHp(int health) {
    this.maxHp = health;
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

  protected void setDamage(int damage) {
    this.damage = damage;
  }

  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

  public void setArmor(int armor) {
    this.armor = armor;
  }

  public void setMoved(boolean isMoved) {
    this.isMoved = isMoved;
  }

  public void setHitTarget(boolean hitTarget) {
    this.hitTarget = hitTarget;
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

  public static Unit createUnitByUnitType(UnitType unitType, PlayerType playerType) {
    return switch (unitType) {
      case KNIGHT -> new Knight(playerType);
      case MAGE -> new Mage(playerType);
      case ARCHER -> new Archer(playerType);
      case HEALER -> new Healer(playerType);
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Unit unit = (Unit) o;
    return maxHp == unit.maxHp &&
            currentHp == unit.currentHp &&
            damage == unit.damage &&
            accuracy == unit.accuracy &&
            armor == unit.armor &&
            isGeneral == unit.isGeneral
            && isMoved == unit.isMoved
            && hitTarget == unit.hitTarget
            && unitType == unit.unitType
            && playerType == unit.playerType
            && attackType == unit.attackType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxHp, currentHp, damage, accuracy, armor, isGeneral, unitType, playerType, attackType, isMoved, hitTarget);
  }
}
