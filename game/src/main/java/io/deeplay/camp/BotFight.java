package io.deeplay.camp;

import io.deeplay.camp.entities.*;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import lombok.SneakyThrows;

public class BotFight {

  private static int winsFirstPlayer = 0;
  private static int winsSecondPlayer = 0;
  private static int countDraw = 0;
  private final int countGame;

  private final int timeSkeep = 250;
  Game game;
  GameAnalisys gameAnalisys;
  BotPlayer botFirst;
  BotPlayer botSecond;
  boolean consoleOut = true;
  boolean outInfoGame;
  String separator = System.getProperty("line.separator");
  PlaceUnitEvent[] placementEvents = new PlaceUnitEvent[12];

  JFrame frame;
  JTextArea area1;
  JPanel contents;

  public BotFight(BotPlayer botFirst, BotPlayer botSecond, int countGame, boolean infoGame) {
    this.botFirst = botFirst;
    this.botSecond = botSecond;
    this.countGame = countGame;
    game = new Game();
    gameAnalisys = new GameAnalisys(countGame);
    this.outInfoGame = infoGame;
    frame = new JFrame();
    frame.setSize(800, 500);
    area1 = new JTextArea(20, 50);
    area1.setFont(new Font("Dialog", Font.PLAIN, 14));
    area1.setTabSize(10);
    contents = new JPanel();
    contents.add(area1);
    frame.add(contents);
    frame.show();
  }

  @SneakyThrows
  public void playGames() throws GameException {
    for (int gameCount = 0; gameCount < countGame; gameCount++) {

      game = new Game();
      placementEvents = new PlaceUnitEvent[12];
      fillPlace(game.gameState.getCurrentBoard());
      game.gameState.setGameStage(GameStage.PLACEMENT_STAGE);
      fillBoard(PlayerType.FIRST_PLAYER);

      game.gameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
      fillBoard(PlayerType.SECOND_PLAYER);

      gameAnalisys.setCurrentBoard(game.gameState.getCurrentBoard().getUnits());

      game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
      game.gameState.getArmyFirst().fillArmy(game.gameState.getCurrentBoard());
      game.gameState.getArmySecond().fillArmy(game.gameState.getCurrentBoard());
      game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);
      consoleView(null);
      for (int z = 0; z < 10; z++) {
        game.gameState.getArmyFirst().isAliveGeneral();
        game.gameState.getArmySecond().isAliveGeneral();
        executeMove(PlayerType.FIRST_PLAYER, gameCount);
        game.changePlayer(new ChangePlayerEvent(game.gameState.getCurrentPlayer()));
        executeMove(PlayerType.SECOND_PLAYER, gameCount);
        if (escapeGame()) {
          break;
        }
        game.changePlayer(new ChangePlayerEvent(game.gameState.getCurrentPlayer()));
      }

      calcResult(gameCount);
    }

    System.out.println("Count Wins 1 Player = " + winsFirstPlayer);
    System.out.println("Count Wins 2 Player  = " + winsSecondPlayer);
    System.out.println("Draws = " + countDraw);

    if (outInfoGame) {
      gameAnalisys.outputInfo();
    }
  }

  public boolean escapeGame() {
    if (botFirst
            .enumerationPlayerUnits(PlayerType.FIRST_PLAYER, game.gameState.getCurrentBoard())
            .size()
        == 0) {
      return true;
    }
    return botSecond
            .enumerationPlayerUnits(PlayerType.SECOND_PLAYER, game.gameState.getCurrentBoard())
            .size()
        == 0;
  }

  public void calcResult(int countGame) {
    if (botFirst
            .enumerationPlayerUnits(PlayerType.FIRST_PLAYER, game.gameState.getCurrentBoard())
            .size()
        > botSecond
            .enumerationPlayerUnits(PlayerType.SECOND_PLAYER, game.gameState.getCurrentBoard())
            .size()) {
      winsFirstPlayer++;
      gameAnalisys.reviewGame(PlayerType.FIRST_PLAYER, game.gameState, countGame);

    } else if (botFirst
            .enumerationPlayerUnits(PlayerType.FIRST_PLAYER, game.gameState.getCurrentBoard())
            .size()
        < botSecond
            .enumerationPlayerUnits(PlayerType.SECOND_PLAYER, game.gameState.getCurrentBoard())
            .size()) {
      winsSecondPlayer++;
      gameAnalisys.reviewGame(PlayerType.SECOND_PLAYER, game.gameState, countGame);

    } else if (botFirst
            .enumerationPlayerUnits(PlayerType.FIRST_PLAYER, game.gameState.getCurrentBoard())
            .size()
        == botSecond
            .enumerationPlayerUnits(PlayerType.SECOND_PLAYER, game.gameState.getCurrentBoard())
            .size()) {
      countDraw++;
      gameAnalisys.reviewGame(PlayerType.DRAW, game.gameState, countGame);
    }
  }

  @SneakyThrows
  public void executeMove(PlayerType playerType, int countGame) throws GameException {
    if (playerType == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = 0; j < game.gameState.getCurrentBoard().getUnits()[i].length / 2; j++) {
          PossibleActions<Position, Position> positionPossibleActionsFirst =
              botFirst.unitsPossibleActions(game.gameState);
          int rand =
              (int) (Math.random() * positionPossibleActionsFirst.get(new Position(i, j)).size());
          Position pos1 = new Position(i, j);
          if (positionPossibleActionsFirst.get(pos1).size() == 0) {
            continue;
          }
          Position pos2 = positionPossibleActionsFirst.get(pos1).get(rand);
          MakeMoveEvent move =
              new MakeMoveEvent(
                  pos1, pos2, game.gameState.getCurrentBoard().getUnit(pos1.x(), pos1.y()));

          game.makeMove(move);
          Thread.sleep(timeSkeep);
          consoleView(move);
        }
      }
    } else if (playerType == PlayerType.SECOND_PLAYER) {
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = game.gameState.getCurrentBoard().getUnits()[i].length / 2;
            j < game.gameState.getCurrentBoard().getUnits()[i].length;
            j++) {
          PossibleActions<Position, Position> positionPossibleActionsSecond =
              botSecond.unitsPossibleActions(game.gameState);
          int rand =
              (int) (Math.random() * positionPossibleActionsSecond.get(new Position(i, j)).size());
          Position pos1 = new Position(i, j);
          if (positionPossibleActionsSecond.get(pos1).size() == 0) {
            continue;
          }
          Position pos2 = positionPossibleActionsSecond.get(pos1).get(rand);
          MakeMoveEvent move =
              new MakeMoveEvent(
                  pos1, pos2, game.gameState.getCurrentBoard().getUnit(pos1.x(), pos1.y()));
          game.makeMove(move);
          Thread.sleep(timeSkeep);
          consoleView(move);
        }
      }
    }
  }

  public void fillBoard(PlayerType playerType) throws GameException {
    int index;
    if (playerType == PlayerType.FIRST_PLAYER) {
      index = 0;
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = 0; j < game.gameState.getCurrentBoard().getUnits()[i].length / 2; j++) {
          game.placeUnit(placementEvents[index++]);
        }
      }
    }
    if (playerType == PlayerType.SECOND_PLAYER) {
      index = 6;
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = game.gameState.getCurrentBoard().getUnits()[i].length / 2;
            j < game.gameState.getCurrentBoard().getUnits()[i].length;
            j++) {
          game.placeUnit(placementEvents[index++]);
        }
      }
    }
  }

  public void fillPlace(Board board) {
    placementEvents[0] =
        new PlaceUnitEvent(
            0, 0, randomUnit(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false);
    placementEvents[1] =
        new PlaceUnitEvent(
            0, 1, new Knight(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false);
    placementEvents[2] =
        new PlaceUnitEvent(
            1, 0, randomUnit(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false);
    placementEvents[3] =
        new PlaceUnitEvent(
            1, 1, new Knight(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false);
    placementEvents[4] =
        new PlaceUnitEvent(
            2, 0, randomUnit(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false);
    placementEvents[5] =
        new PlaceUnitEvent(
            2, 1, new Knight(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, false, false);
    placementEvents[6] =
        new PlaceUnitEvent(
            0, 2, new Knight(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false);
    placementEvents[7] =
        new PlaceUnitEvent(
            0, 3, randomUnit(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false);
    placementEvents[8] =
        new PlaceUnitEvent(
            1, 2, new Knight(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false);
    placementEvents[9] =
        new PlaceUnitEvent(
            1, 3, randomUnit(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false);
    placementEvents[10] =
        new PlaceUnitEvent(
            2, 2, new Knight(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false);
    placementEvents[11] =
        new PlaceUnitEvent(
            2, 3, randomUnit(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, false, false);

    int randUnitFirst = rnd(0, 5);
    while (placementEvents[randUnitFirst].getUnit().getUnitType() == UnitType.HEALER) {
      randUnitFirst = rnd(0, 5);
    }
    int randUnitSecond = rnd(6, 11);
    while (placementEvents[randUnitSecond].getUnit().getUnitType() == UnitType.HEALER) {
      randUnitSecond = rnd(6, 11);
    }
    placementEvents[randUnitFirst].getUnit().setGeneral(true);
    placementEvents[randUnitSecond].getUnit().setGeneral(true);
  }

  public static Unit randomUnit(PlayerType playerType) {
    Unit unit;
    switch (UnitType.getRandom()) {
      case MAGE -> unit = new Mage(playerType);
      case ARCHER -> unit = new Archer(playerType);
      case HEALER -> unit = new Healer(playerType);
      case KNIGHT -> unit = new Healer(playerType);
      default -> unit = new Archer(playerType);
    }
    return unit;
  }

  public void consoleView(MakeMoveEvent move) throws IOException {
    if (consoleOut) {
      area1.setText(null);
      if (move == null) {
        area1.append("BEGIN NEW GAME!");
      }
      area1.append(separator);
      area1.append(separator);
      String s = "20";
      area1.append(String.format("%-" + s + "s", "3"));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(0, 3).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(0, 3).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(0, 3).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(1, 3).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(1, 3).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(1, 3).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(2, 3).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(2, 3).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(2, 3).getNowHp()));
      area1.append(separator);
      area1.append(separator);
      area1.append(String.format("%-" + s + "s", "2"));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(0, 2).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(0, 2).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(0, 2).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(1, 2).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(1, 2).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(1, 2).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(2, 2).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(2, 2).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(2, 2).getNowHp()));
      area1.append(separator);
      area1.append(separator);
      area1.append(String.format("%-" + s + "s", "1"));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(0, 1).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(0, 1).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(0, 1).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(1, 1).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(1, 1).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(1, 1).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(2, 1).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(2, 1).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(2, 1).getNowHp()));
      area1.append(separator);
      area1.append(separator);
      area1.append(String.format("%-" + s + "s", "0"));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(0, 0).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(0, 0).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(0, 0).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(1, 0).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(1, 0).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(1, 0).getNowHp()));
      area1.append(
          String.format(
              "%-" + s + "s",
              markIsMoved(game.gameState.getCurrentBoard().getUnit(2, 0).getMoved())
                  + game.gameState.getCurrentBoard().getUnit(2, 0).getUnitType().name()
                  + ""
                  + game.gameState.getCurrentBoard().getUnit(2, 0).getNowHp()));
      area1.append(separator);
      area1.append(separator);
      area1.append(String.format("%-25s", "#"));
      area1.append(String.format("%-25s", "0"));
      area1.append(String.format("%-27s", "1"));
      area1.append(String.format("%-26s", "2"));
      area1.append(separator);
      area1.append(separator);
      if (move != null) {
        if (move.getAttacker().getUnitType() == UnitType.MAGE) {
          area1.append(
              "Unit "
                  + move.getAttacker().getUnitType().name()
                  + "("
                  + move.getFrom().x()
                  + ","
                  + move.getFrom().y()
                  + ") attack all enemys units");
        } else if (move.getAttacker().getUnitType() == UnitType.HEALER) {
          area1.append(
              "Unit "
                  + move.getAttacker().getUnitType().name()
                  + "("
                  + move.getFrom().x()
                  + ","
                  + move.getFrom().y()
                  + ") heal "
                  + move.getAttacker().getUnitType().name()
                  + "("
                  + move.getTo().x()
                  + ","
                  + move.getTo().y()
                  + ")");
        } else {
          area1.append(
              "Unit "
                  + move.getAttacker().getUnitType().name()
                  + "("
                  + move.getFrom().x()
                  + ","
                  + move.getFrom().y()
                  + ") attack "
                  + move.getAttacker().getUnitType().name()
                  + "("
                  + move.getTo().x()
                  + ","
                  + move.getTo().y()
                  + ")");
        }
        area1.append(separator);
      }
    }
  }

  public static int rnd(int min, int max) {
    max -= min;
    return (int) (Math.random() * ++max) + min;
  }

  private String markIsMoved(boolean isMoved) {
    String result = "?";
    if (isMoved) {
      result = "!";
    }
    return result;
  }
}
