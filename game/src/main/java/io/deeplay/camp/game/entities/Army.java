package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class Army {
  public PlayerType owner;
  public UnitType generalType;
  public boolean isAliveGeneral = false;
  public boolean isBuffed = false;
  Unit[] units;

  public Army(PlayerType owner) {
    this.owner = owner;
    units = new Unit[6];
  }

  public void fillArmy(Board board) {
    int index = 0;
    if (this.owner == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < board.getUnits().length; i++) {
        for (int j = 0; j < board.getUnits()[i].length / 2; j++) {
          if (board.getUnit(i, j) != null) {
            units[index++] = board.getUnit(i, j);
          }
        }
      }
    }
    if (this.owner == PlayerType.SECOND_PLAYER) {
      for (int i = 0; i < board.getUnits().length; i++) {
        for (int j = board.getUnits()[i].length / 2; j < board.getUnits()[i].length; j++) {
          if (board.getUnit(i, j) != null) {
            units[index++] = board.getUnit(i, j);
          }
        }
      }
    }
  }

  // Обновление возможности ходить для юнитов данной армии
  public void updateArmy() {
    for (Unit unit : units) {
      unit.setMoved(false);
      unit.setHitTarget(false);
    }
  }

  public boolean isAliveGeneral() {
    isAliveGeneral = false;
    for (Unit unit : units) {
      if (unit == null) {
        continue;
      }
      if (unit.isGeneral() && unit.isAlive()) {
        generalType = unit.getUnitType();
        isAliveGeneral = true;
        break;
      }
    }
    if (isAliveGeneral) {
      if (!isBuffed) {
        for (Unit unit : units) {
          unit.applyBuff(unit, generalType);
        }
      }
      isBuffed = true;
    } else if (isBuffed) {
      for (Unit unit : units) {
        unit.removeBuff(unit, generalType);
      }
      isBuffed = false;
    }
    return isAliveGeneral;
  }

  public boolean hasGeneral() {
    for (Unit unit : units) {
      if (unit != null && unit.isGeneral()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Army army = (Army) o;
    return isAliveGeneral == army.isAliveGeneral && isBuffed == army.isBuffed && owner == army.owner && generalType == army.generalType && Objects.deepEquals(units, army.units);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, generalType, isAliveGeneral, isBuffed, Arrays.hashCode(units));
  }
}
