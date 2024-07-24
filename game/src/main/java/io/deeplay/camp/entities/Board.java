package io.deeplay.camp.entities;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private boolean isFullPart(int startRow, int endRow) {
    for (int i = startRow; i < endRow; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        if (isEmptyCell(j, i)) {
          logger.atInfo().log("Empty cell (X-{},Y-{})", j, i);
          return false;
        }
      }
    }
    return true;
  }

  public boolean isFullFirstPlayerPart() {
    return isFullPart(0, Board.ROWS / 2);
  }

  public boolean isFullSecondPlayerPart() {
    return isFullPart(Board.ROWS / 2, Board.ROWS);
  }

  public List<Position> enumerateUnits(int startRow, int endRow) {
    List<Position> unitPositions = new ArrayList<>();
    for (int i = startRow; i < endRow; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        if (isEmptyCell(j, i)) {
          logger.atInfo().log("Empty cell (X-{},Y-{})", j, i);
          continue;
        }
        if (getUnit(j, i).isAlive()) {
          unitPositions.add(new Position(j, i));
        } else {
          logger.atInfo().log("Dead unit (X-{},Y-{})", j, i);
        }
      }
    }
    return unitPositions;
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
