package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Board {
  // y
  public static final int ROWS = 4;
  // x
  public static final int COLUMNS = 3;

  private Unit[][] units;

  public Board() {
    // Значения         X       Y
    units = new Unit[COLUMNS][ROWS];
  }

  public void setUnit(int x, int y, Unit unit) {
    units[x][y] = unit;
  }

  public Unit getUnit(int x, int y) {
    return units[x][y];
  }

  public boolean isFullBoard() {
    for (Unit[] row : units) {
      for (Unit unit : row) {
        if (unit == null) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isTakenCell(int x, int y) {
    return units[x][y] != null;
  }
}
