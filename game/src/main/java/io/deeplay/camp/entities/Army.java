package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;
import lombok.Getter;

@Getter
public class Army {
  Unit[] units;
  public PlayerType owner;
  public UnitType generalType;
  public boolean isAliveGeneral = false;
  public boolean isBuffed = false;

  public Army(PlayerType owner) {
    this.owner = owner;
    units = new Unit[6];
  }

  public void fillArmy(Board board) {
    int index = 0;
    if (this.owner == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < board.getUnits().length; i++) {
        for (int j = 0; j < board.getUnits()[i].length / 2; j++) {
          units[index++] = board.getUnit(i, j);
        }
      }
    }
    if (this.owner == PlayerType.SECOND_PLAYER) {
      for (int i = 0; i < board.getUnits().length; i++) {
        for (int j = board.getUnits().length / 2; j < board.getUnits()[i].length; j++) {
          units[index++] = board.getUnit(i, j);
        }
      }
    }
  }

  public boolean isAliveGeneral() {
    isAliveGeneral = false;
    for (Unit unit : units) {
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
