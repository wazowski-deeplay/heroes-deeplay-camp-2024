package io.deeplay.camp;

import io.deeplay.camp.entities.*;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.*;
import java.awt.*;
import javax.swing.*;

public class BotFight {

  private static int winsFirstPlayer = 0;
  private static int winsSecondPlayer = 0;
  private static int countDraw = 0;
  private final int countGame;

  private final int timeSkeep = 50;
  Game game;
  GameAnalisys gameAnalisys;
  BotPlayer botFirst;
  BotPlayer botSecond;
  boolean consoleOut = true;
  boolean outInfoGame;
  String separator = System.getProperty("line.separator");
  JFrame frame;
  JTextArea area1;
  JPanel contents;

  public BotFight(BotPlayer botFirst, BotPlayer botSecond, int countGame, boolean infoGame) {
    this.botFirst = botFirst;
    this.botSecond = botSecond;
    this.countGame = countGame;
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

  public void playGames() throws GameException, InterruptedException {
    for (int gameCount = 0; gameCount < countGame; gameCount++) {

      game = new Game();

      game.gameState.setGameStage(GameStage.PLACEMENT_STAGE);
      executePlace(game.gameState.getCurrentPlayer(), gameCount);
      game.gameState.changeCurrentPlayer();
      executePlace(game.gameState.getCurrentPlayer(), gameCount);
      game.gameState.changeCurrentPlayer();
      gameAnalisys.setCurrentBoard(game.gameState.getCurrentBoard().getUnits());

      game.gameState.getArmyFirst().fillArmy(game.gameState.getCurrentBoard());
      game.gameState.getArmySecond().fillArmy(game.gameState.getCurrentBoard());
      game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);
      while (game.gameState.getGameStage() != GameStage.ENDED) {
        executeMove(game.gameState.getCurrentPlayer(), gameCount);
        game.changePlayer(new ChangePlayerEvent(game.gameState.getCurrentPlayer()));
        executeMove(game.gameState.getCurrentPlayer(), gameCount);
        game.changePlayer(new ChangePlayerEvent(game.gameState.getCurrentPlayer()));
      }

      calcResult(gameCount);

      game = null;
    }

    System.out.println("Count Wins 1 Player = " + winsFirstPlayer);
    System.out.println("Count Wins 2 Player  = " + winsSecondPlayer);
    System.out.println("Draws = " + countDraw);

    if (outInfoGame) {
      gameAnalisys.outputInfo();
    }
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

  public void executeMove(PlayerType playerType, int countGame)
      throws GameException, InterruptedException {
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
          outInFrame(move);
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
          outInFrame(move);
        }
      }
    }
  }

  public void executePlace(PlayerType playerType, int countGame)
      throws GameException, InterruptedException {
    if (playerType == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = 0; j < game.gameState.getCurrentBoard().getUnits()[i].length / 2; j++) {
          PossibleActions<Position, Unit> positionPossiblePlacementFirst =
              botFirst.unitsPossiblePlacement(game.gameState);

          Position pos1 = new Position(i, j);
          int rand = (int) (Math.random() * positionPossiblePlacementFirst.get(pos1).size());
          if (positionPossiblePlacementFirst.get(pos1).size() == 0) {
            continue;
          }
          boolean inProcess = true;
          boolean general = false;
          if (botFirst
                      .enumerationPlayerUnits(
                          PlayerType.FIRST_PLAYER, game.gameState.getCurrentBoard())
                      .size()
                  + 1
              == 6) {
            inProcess = false;
            general = true;
          }
          PlaceUnitEvent place =
              new PlaceUnitEvent(
                  pos1.x(),
                  pos1.y(),
                  positionPossiblePlacementFirst.get(pos1).get(rand),
                  PlayerType.FIRST_PLAYER,
                  inProcess,
                  general);

          game.placeUnit(place);
          Thread.sleep(timeSkeep);
          outInFrame(null);
        }
      }
    } else if (playerType == PlayerType.SECOND_PLAYER) {
      for (int i = 0; i < game.gameState.getCurrentBoard().getUnits().length; i++) {
        for (int j = game.gameState.getCurrentBoard().getUnits()[i].length / 2;
            j < game.gameState.getCurrentBoard().getUnits()[i].length;
            j++) {
          PossibleActions<Position, Unit> positionPossiblePlacementSecond =
              botSecond.unitsPossiblePlacement(game.gameState);
          Position pos1 = new Position(i, j);
          int rand = (int) (Math.random() * positionPossiblePlacementSecond.get(pos1).size());
          if (positionPossiblePlacementSecond.get(pos1).size() == 0) {
            continue;
          }
          boolean inProcess = true;
          boolean general = false;
          if (botFirst
                      .enumerationPlayerUnits(
                          PlayerType.SECOND_PLAYER, game.gameState.getCurrentBoard())
                      .size()
                  + 1
              == 6) {
            inProcess = false;
            general = true;
          }
          PlaceUnitEvent place =
              new PlaceUnitEvent(
                  pos1.x(),
                  pos1.y(),
                  positionPossiblePlacementSecond.get(pos1).get(rand),
                  PlayerType.SECOND_PLAYER,
                  inProcess,
                  general);

          game.placeUnit(place);
          Thread.sleep(timeSkeep);
          outInFrame(null);
        }
      }
    }
  }

  // Вывод в окно JFrame
  public void outInFrame(MakeMoveEvent move) {
    if (consoleOut) {
      area1.setText(null);
      if (move == null) {
        area1.append("BEGIN NEW GAME!");
      }
      area1.append(separator);
      area1.append(separator);
      String s = "20";
      for (int row = 3; row >= 0; row--) {
        area1.append(String.format("%-" + s + "d", row));
        for (int column = 0; column < 3; column++) {
          area1.append(
              String.format(
                  "%-" + s + "s",
                  outUnitIsMoved(game.gameState.getCurrentBoard().getUnit(column, row))
                      + outUnitInfo(game.gameState.getCurrentBoard().getUnit(column, row))));
        }
        area1.append(separator);
        area1.append(separator);
      }

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

  // Методы для отображения стринговой информации о юните
  private String outUnitIsMoved(Unit unit) {
    String result = "?";
    if (unit == null) {
      return "";
    }
    if (unit.getMoved()) {
      result = "!";
    }
    return result;
  }

  private String outUnitInfo(Unit unit) {
    String result = "?";
    if (unit == null) {
      return result = "------";
    }
    switch (unit.getUnitType()) {
      case KNIGHT -> result = "Knight" + unit.getCurrentHp();
      case ARCHER -> result = "Archer" + unit.getCurrentHp();
      case MAGE -> result = "Wizard" + unit.getCurrentHp();
      case HEALER -> result = "Healer" + unit.getCurrentHp();
      default -> result = "------";
    }
    return result;
  }
}
