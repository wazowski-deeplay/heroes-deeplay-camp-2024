package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public class GameLogic {
  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement) {

    Board board = gameState.getCurrentBoard();

    int x = placement.getColums();
    int y = placement.getRows();
    boolean result = false;

    // Проверка есть ли место на доске
    if (board.isFullBoard()) {
      return result;
    }

    // Проверка на генерала,а так же что их не 6 шт
    // генерала нельзя дать одному и тому же юниту
    // Пройтись по массиву enumirationsPlayersUnits
    // и првоерить наличее хотябы одного генерала
    // прийдётся дополнять
    // if (placement.getUnit().isGeneral()){
    // }

    // Проверка занята ли клетка
    if (board.isTakenCell(x, y)) {
      return result;
    }

    // Проверка на сторону юнита
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      if (y < (Board.ROWS / 2)) {
        return result = true;
      } else {
        return result;
      }
    } else {
      if (y > ((Board.ROWS / 2) - 1) && y < Board.ROWS) {
        return result = true;
      } else {
        return result;
      }
    }
  }

  public static boolean isValidChangePlayer(GameState gameState, ChangePlayerEvent changePlayer) {
    return gameState.getCurrentPlayer() == changePlayer.getRequester()
        && gameState.getGameStage() != GameStage.PLACEMENT_STAGE;
  }

  public static boolean isValidMove(GameState gameState, MakeMoveEvent move) {
    boolean result = false;
    Position from = move.getFrom();
    Position to = move.getTo();
    Unit attacker = move.getAttacker();

    boolean fullUnitInRow = fullUnitMeleeRow(gameState, from, to, attacker);
    boolean oneUnitInRow = oneUnitMeleeRow(gameState, from, to, attacker);
    boolean nullUnitInRow = nullUnitMeleeRow(gameState, from, to, attacker);

    if (gameState.getCurrentBoard().getUnit(to.x(), to.y()).getPlayerType()
        != attacker.getPlayerType()) {
      if (attacker.getUnitType() == UnitType.KNIGHT) {
        int radius = 1;
        if (oneUnitInRow || nullUnitInRow) {
          radius = 2;
        }
        if (Math.abs(from.y() - to.y()) <= radius && Math.abs(from.x() - to.x()) <= radius) {
          if (fullUnitInRow || oneUnitInRow || nullUnitInRow) {
            result = true;
          }
        }
      }
      if (attacker.getUnitType() == UnitType.ARCHER) {
        result = true;
      }
      if (attacker.getUnitType() == UnitType.MAGE) {
        result = true;
      }
    } else {
      if (attacker.getUnitType() == UnitType.HEALER) {
        result = true;
      }
    }
    return result;
  }

  // Методы для проверки количества вражеских юнитов во вражеской ближней линии
  public static boolean fullUnitMeleeRow(
      GameState gameState, Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() + 1) > 1
            && to.y() == from.y() + 1)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() - 1) > 1
            && to.y() == from.y() - 1);
  }

  public static boolean oneUnitMeleeRow(
      GameState gameState, Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() + 1) == 1
            && to.y() == from.y() + 1)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() - 1) == 1
            && to.y() == from.y() - 1);
  }

  public static boolean nullUnitMeleeRow(
      GameState gameState, Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() + 1) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() - 1) == 0);
  }
}
