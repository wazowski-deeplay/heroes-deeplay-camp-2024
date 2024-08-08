package io.deeplay.camp.botfarm;

import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Getter
public class GameAnalisys {

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
  String separator = System.lineSeparator();
  File fileOutput;
  BufferedWriter writer;
  int gameId;

  public GameAnalisys(int countGame, int gameId) throws IOException {
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
    this.gameId = gameId;
    String path = "C:\\Deeplay\\deeplay-heroes\\botfarm\\src\\main\\java\\io\\deeplay\\camp\\botfarm";
    fileOutput = new File(path + "\\resultgame"+gameId+".txt");
    writer = new BufferedWriter(new FileWriter(fileOutput,true));

  }

  // Подсчитывает результаты одной игры
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

  // Выводит информацию о всех прошедших играх
  public void outputInfo() throws IOException {
    int tab = 30;
    int tab2 = 30;
    int winFirst = 0;
    int winSecond = 0;
    int winDraw = 0;
    for(PlayerType playerType : winners){
      if(playerType == PlayerType.FIRST_PLAYER){
        winFirst++;
      }
      if(playerType == PlayerType.SECOND_PLAYER){
        winSecond++;
      }
      if(playerType == PlayerType.DRAW){
        winDraw++;
      }
    }
    writer.append(String.format("Id fight = " + gameId));
    writer.append(separator);
    writer.append(String.format("First player wins = " + winFirst + ", his winrate = " + ((double)winFirst/countGame)*100));
    writer.append(separator);
    writer.append(String.format("Second player wins = " + winSecond + ", his winrate = " + ((double)winSecond/countGame)*100));
    writer.append(separator);
    writer.append(String.format("Draw wins = " + winDraw + ", winrate draws = " + ((double)winDraw/countGame)*100));
    writer.append(separator);
    writer.append(String.format("%-" + tab2 + "s", "game, #"));
    writer.append(String.format("%-" + tab2 + "s", "Winners"));
    writer.append(String.format("%-" + tab2 + "s", "GenerP1"));
    writer.append(String.format("%-" + tab2 + "s", "GenerP2"));
    writer.append(String.format("%-" + tab2 + "s", "CountKn1"));
    writer.append(String.format("%-" + tab2 + "s", "CountAr1"));
    writer.append(String.format("%-" + tab2 + "s", "CountMa1"));
    writer.append(String.format("%-" + tab2 + "s", "CountHe1"));
    writer.append(String.format("%-" + tab2 + "s", "CountKn2"));
    writer.append(String.format("%-" + tab2 + "s", "CountAr2"));
    writer.append(String.format("%-" + tab2 + "s", "CountMa2"));
    writer.append(String.format("%-" + tab2 + "s", "CountHe2"));

    for (int i = 0; i < countGame; i++) {
      writer.append(separator);
      writer.append(String.format("%-" + tab + "d", i + 1));
      writer.append(String.format("%-" + tab + "s", winners[i].name()));
      writer.append(String.format("%-" + tab + "s", generalType[i][0].name()));
      writer.append(String.format("%-" + tab + "s", generalType[i][1].name()));
      writer.append(String.format("%-" + tab + "d", favorKnightFirst[i][0]));
      writer.append(String.format("%-" + tab + "d", favorArcherFirst[i][0]));
      writer.append(String.format("%-" + tab + "d", favorMageFirst[i][0]));
      writer.append(String.format("%-" + tab + "d", favorHealerFirst[i][0]));
      writer.append(String.format("%-" + tab + "d", favorKnightSecond[i][0]));
      writer.append(String.format("%-" + tab + "d", favorArcherSecond[i][0]));
      writer.append(String.format("%-" + tab + "d", favorMageSecond[i][0]));
      writer.append(String.format("%-" + tab + "d", favorHealerSecond[i][0]));
    }
    writer.append(separator);
    writer.close();
  }

  public void setCurrentBoard(Unit[][] board) {
    currentBoard = new Board(board);
  }
}
