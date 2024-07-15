package io.deeplay.camp.entities;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Board {
  public static final int ROWS = 6;
  public static final int COLUMNS = 3;

  private Unit[][] units;

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
}
