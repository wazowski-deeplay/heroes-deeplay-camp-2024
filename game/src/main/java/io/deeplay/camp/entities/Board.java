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

  public boolean isFullFirstPlyerPart() {
    for (int i = 0; i < Board.ROWS / 2; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        if (getUnit(j, i) == null) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isFullSecondPlyerPart() {
    for (int i = 2; i < Board.ROWS; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        if (getUnit(j, i) == null) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isFullBoard() {
    if (isFullFirstPlyerPart() && isFullSecondPlyerPart()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isTakenCell(int x, int y) {
    return units[x][y] != null;
  }

  public int countUnitsRow(int row) {
    int count = 0;
    for (int i = 0; i < COLUMNS; i++) {
      if (isEmptyCell(i, row)) {
        continue;
      }
      if (units[i][row].isAlive()) {
        count++;
      }
    }
    return count;
  }

  public boolean isEmptyCell(int x, int y) {
    return units[x][y] == null;
  }

  public Unit[][] getUnits() {
    return units;
  }
}
