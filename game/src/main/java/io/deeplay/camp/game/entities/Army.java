package io.deeplay.camp.game.entities;

import io.deeplay.camp.game.mechanics.PlayerType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  public Army(Army army) {
    this.owner = army.owner;
    this.generalType = army.generalType;
    this.isAliveGeneral = army.isAliveGeneral;
    this.isBuffed = army.isBuffed;
    this.units = new Unit[army.units.length];
    for (int i = 0; i < army.units.length; i++) {
      Unit originalUnit = army.units[i];
      if (originalUnit != null) {
        try {
          Constructor<? extends Unit> constructor =
              originalUnit.getClass().getConstructor(originalUnit.getClass());
          this.units[i] = constructor.newInstance(originalUnit);
        } catch (NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
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
  public void updateArmyMoves() {
    for (Unit unit : units) {
      unit.setMoved(false);
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
}
