package io.deeplay.camp;

import io.deeplay.camp.entities.*;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.Getter;

@Getter
public class GameAnalysis {

  int[][] favorKnightFirst;
  int[][] favorHealerFirst;
  int[][] favorArcherFirst;
  int[][] favorMageFirst;
  int[][] favorKnightSecond;
  int[][] favorHealerSecond;
  int[][] favorArcherSecond;
  int[][] favorMageSecond;
  UnitType[][] generalType;
  PlayerType[] winners;
  Board currentBoard;
  int countGame;

  public GameAnalysis(int countGame) {
    this.countGame = countGame;
    favorKnightFirst = new int[countGame][3];
    favorHealerFirst = new int[countGame][3];
    favorArcherFirst = new int[countGame][3];
    favorMageFirst = new int[countGame][3];
    favorKnightSecond = new int[countGame][3];
    favorHealerSecond = new int[countGame][3];
    favorArcherSecond = new int[countGame][3];
    favorMageSecond = new int[countGame][3];
    generalType = new UnitType[countGame][2];
    winners = new PlayerType[countGame];
  }

  public void reviewMove(MakeMoveEvent move, GameState gameState, int countGame) {
    int xfrom = move.getFrom().x();
    int yfrom = move.getFrom().y();
    int xto = move.getTo().x();
    int yto = move.getTo().y();
    Board board = gameState.getCurrentBoard();
    Unit attacker = board.getUnit(xfrom, yfrom);
    if (attacker.getUnitType() == UnitType.MAGE) {
      if (attacker.getPlayerType() == PlayerType.FIRST_PLAYER) {
        favorMageFirst[countGame][2]++;
        for (int i = 0; i < board.getUnits().length; i++) {
          for (int j = board.getUnits()[i].length / 2; j < board.getUnits()[i].length; j++) {
            if (currentBoard.getUnit(i, j).getNowHp() != board.getUnit(i, j).getNowHp()) {
              favorMageFirst[countGame][1]++;
            }
          }
        }
      } else {
        favorMageSecond[countGame][2]++;
        for (int i = 0; i < board.getUnits().length; i++) {
          for (int j = 0; j < board.getUnits()[i].length / 2; j++) {
            if (currentBoard.getUnit(i, j).getNowHp() != board.getUnit(i, j).getNowHp()) {
              favorMageSecond[countGame][1]++;
            }
          }
        }
      }
    }
    if (attacker.getPlayerType() == PlayerType.FIRST_PLAYER) {
      if (attacker.getUnitType() == UnitType.KNIGHT) {
        favorKnightFirst[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorKnightFirst[countGame][1]++;
        }
      }
      if (attacker.getUnitType() == UnitType.ARCHER) {
        favorArcherFirst[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorArcherFirst[countGame][1]++;
        }
      }
      if (attacker.getUnitType() == UnitType.HEALER) {
        favorHealerFirst[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorHealerFirst[countGame][1]++;
        }
      }
    }
    if (attacker.getPlayerType() == PlayerType.SECOND_PLAYER) {
      if (attacker.getUnitType() == UnitType.KNIGHT) {
        favorKnightSecond[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorKnightSecond[countGame][1]++;
        }
      }
      if (attacker.getUnitType() == UnitType.ARCHER) {
        favorArcherSecond[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorArcherSecond[countGame][1]++;
        }
      }
      if (attacker.getUnitType() == UnitType.HEALER) {
        favorHealerSecond[countGame][2]++;
        if (currentBoard.getUnit(xto, yto).getNowHp() != board.getUnit(xto, yto).getNowHp()) {
          favorHealerSecond[countGame][1]++;
        }
      }
    }
  }

  public void reviewGame(PlayerType playerWinner, GameState gameState, int countGame) {
    int[] knightCount = new int[2];
    int[] archerCount = new int[2];
    int[] mageCount = new int[2];
    int[] healerCount = new int[2];
    winners[countGame] = playerWinner;

    generalType[countGame][0] = gameState.getArmyFirst().getGeneralType();
    generalType[countGame][1] = gameState.getArmySecond().getGeneralType();
    for (int i = 0; i < 6; i++) {
      switch (gameState.getArmyFirst().getUnits()[i].getUnitType()) {
        case KNIGHT -> knightCount[0]++;
        case HEALER -> healerCount[0]++;
        case ARCHER -> archerCount[0]++;
        case MAGE -> mageCount[0]++;
        default -> mageCount[0] += 0;
      }
      switch (gameState.getArmySecond().getUnits()[i].getUnitType()) {
        case KNIGHT -> knightCount[1]++;
        case HEALER -> healerCount[1]++;
        case ARCHER -> archerCount[1]++;
        case MAGE -> mageCount[1]++;
        default -> mageCount[0] += 0;
      }
    }

    favorKnightFirst[countGame][0] = knightCount[0];
    favorArcherFirst[countGame][0] = archerCount[0];
    favorHealerFirst[countGame][0] = healerCount[0];
    favorMageFirst[countGame][0] = mageCount[0];
    favorKnightSecond[countGame][0] = knightCount[1];
    favorArcherSecond[countGame][0] = archerCount[1];
    favorHealerSecond[countGame][0] = healerCount[1];
    favorMageSecond[countGame][0] = mageCount[1];
  }

  public void outputInfo() {
    int tab = 30;
    int tab2 = 30;
    System.out.println();
    System.out.format("%-" + tab2 + "s", "game, #");
    System.out.format("%-" + tab2 + "s", "Winners");
    System.out.format("%-" + tab2 + "s", "GenerP1");
    System.out.format("%-" + tab2 + "s", "GenerP2");
    System.out.format("%-" + tab2 + "s", "CountKn1");
    System.out.format("%-" + tab2 + "s", "CountAr1");
    System.out.format("%-" + tab2 + "s", "CountMa1");
    System.out.format("%-" + tab2 + "s", "CountHe1");
    System.out.format("%-" + tab2 + "s", "CountKn2");
    System.out.format("%-" + tab2 + "s", "CountAr2");
    System.out.format("%-" + tab2 + "s", "CountMa2");
    System.out.format("%-" + tab2 + "s", "CountHe2");

    for (int i = 0; i < countGame; i++) {
      System.out.println();
      System.out.format("%-" + tab + "d", i + 1);
      System.out.format("%-" + tab + "s", winners[i].name());
      System.out.format("%-" + tab + "s", generalType[i][0].name());
      System.out.format("%-" + tab + "s", generalType[i][1].name());
      System.out.format("%-" + tab + "d", favorKnightFirst[i][0]);
      System.out.format("%-" + tab + "d", favorArcherFirst[i][0]);
      System.out.format("%-" + tab + "d", favorMageFirst[i][0]);
      System.out.format("%-" + tab + "d", favorHealerFirst[i][0]);
      System.out.format("%-" + tab + "d", favorKnightSecond[i][0]);
      System.out.format("%-" + tab + "d", favorArcherSecond[i][0]);
      System.out.format("%-" + tab + "d", favorMageSecond[i][0]);
      System.out.format("%-" + tab + "d", favorHealerSecond[i][0]);
    }
    System.out.println();
  }

  public void setCurrentBoard(Unit[][] board) {
    currentBoard = new Board(board);
  }
}
