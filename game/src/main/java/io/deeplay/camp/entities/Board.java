package io.deeplay.camp.entities;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Board {
  public static final int ROWS = 4;
  public static final int COLUMNS = 3;

  private Unit[][] units;

  public Board() {
    units = new Unit[COLUMNS][ROWS];
  }

  public void setUnit(int x, int y, Unit unit) {
    units[x][y] = unit;
  }

  public Unit getUnit(int x, int y) {
    return units[x][y];
  }

  public boolean isFull() {
    for (Unit[] row : units) {
      for (Unit unit : row) {
        if (unit == null) {
          return false;
        }
      }
    }
    return true;
  }

  public int countUnitsRow(int row) {
    int count = 0;
    for (int i = 0; i < COLUMNS; i++) {
      if (units[i][row].isAlive()) {
        count++;
      }
    }
    return count;
  }

  public Unit[][] getUnits() {
    return units;
  }
}
