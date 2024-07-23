package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogic {
  private static final Logger logger = LoggerFactory.getLogger(GameLogic.class);

  /**
   * Метод проверяет корректность постановки юнита на доску
   *
   * @param gameState Актуальное игровое состояние
   * @param placement Событие расстановки
   * @throws GameException Не корректные координаты, за пределами поля или с чужой стороны. Не
   *     заполненное игровое поле. Отсутствие генерала в расстановке
   */
  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement)
      throws GameException {

    Board board = gameState.getCurrentBoard();

    int x = placement.getColumns();
    int y = placement.getRows();
    boolean result = false;

    logger.atInfo().log("Checking placement for unit {} at ({}, {})", placement.getUnit(), x, y);

    // Проверка стартующая когда расстановка по мнению игрока окончена
    if (!placement.isInProcess()) {
      board.setUnit(x, y, placement.getUnit());
      logger.atInfo().log("Placement process finished. Checking board and general presence.");
      // Проверка на то что на доске есть генерал
      if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
        if (!board.isFullFirstPlayerPart()) {
          logger.atError().log("First player board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!GameLogic.currentPlayerHaveGeneral(board, gameState)) {
          logger.atError().log("First player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      } else {
        if (!board.isFullSecondPlayerPart()) {
          logger.atError().log("Second player's board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!GameLogic.currentPlayerHaveGeneral(board, gameState)) {
          logger.atError().log("Second player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      }
    }

    if (x > Board.COLUMNS) {
      logger.atError().log("Placement coordinates ({}, {}) are out of board bounds.", x, y);
      throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
    }
    if (y > Board.ROWS) {
      logger.atError().log("Placement coordinates ({}, {}) are out of board bounds.", x, y);
      throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
    }
    // Проверка на сторону юнита
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      if (y < (Board.ROWS / 2)) {
        result = true;
        logger.atInfo().log("Placement valid for First Player at ({}, {}).", x, y);
      } else {
        logger.atError().log("Placement invalid for First Player at ({}, {}).", x, y);
        throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
      }
    } else {
      if (y > ((Board.ROWS / 2) - 1) && y < Board.ROWS) {
        logger.atInfo().log("Placement valid for Second Player at ({}, {}).", x, y);
        result = true;
      } else {
        logger.atError().log("Placement invalid for Second Player at ({}, {}).", x, y);
        throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
      }
    }
    return result;
  }

  /**
   * Метод проверяет событие перехода хода другому игроку.
   *
   * @param gameState Актуальное игровое состояние.
   * @param changePlayerEvent Событие передачи хода.
   * @throws GameException Если расстановка не завершена, либо переход запросил не тот игрок.
   */
  public static void isValidChangePlayer(GameState gameState, ChangePlayerEvent changePlayerEvent)
      throws GameException {
    if (gameState.getCurrentPlayer() == changePlayerEvent.getRequester()
        && gameState.getGameStage() != GameStage.PLACEMENT_STAGE) {
      logger.atInfo().log("{} has completed his turn", changePlayerEvent.getRequester().name());
    } else {
      logger.atInfo().log(
          "{} passes the move out of his turn", changePlayerEvent.getRequester().name());
      throw new GameException(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE);
    }
  }

  /**
   * Метод проверяет событие совершение хода одним юнитом по отношению к другому.
   *
   * @param gameState Актуальное игровое состояние.
   * @param move Событие совершения хода юнитом.
   * @throws GameException Если атакующий юнит атакует своего союзника, либо бьёт по мёртвому.
   */
  public static boolean isValidMove(GameState gameState, MakeMoveEvent move) throws GameException {
    boolean result = false;
    Position from = move.getFrom();
    Position to = move.getTo();
    Unit attacker = move.getAttacker();

    boolean fullUnitInRow = fullUnitMeleeRow(gameState, from, to, attacker);
    boolean oneUnitInRow = oneUnitMeleeRow(gameState, from, to, attacker);
    boolean nullUnitInRow = nullUnitMeleeRow(gameState, from, to, attacker);
    boolean nullUnitInNextRow = nullUnitNextMeleeRow(gameState, from, to, attacker);
    boolean attackEnemyUnit =
        gameState.getCurrentBoard().getUnit(to.x(), to.y()).getPlayerType()
            != attacker.getPlayerType();
    boolean isAliveDefender = gameState.getCurrentBoard().getUnit(to.x(), to.y()).isAlive();

    if (attacker.getUnitType() == UnitType.KNIGHT) {
      if (attackEnemyUnit && isAliveDefender) {
        int radius = 1;
        if (oneUnitInRow || nullUnitInRow) {
          radius = 2;
        }
        if (nullUnitInNextRow) {
          radius = 3;
        }
        if (Math.abs(from.y() - to.y()) <= radius && Math.abs(from.x() - to.x()) <= radius) {
          if (fullUnitInRow || oneUnitInRow || nullUnitInRow || nullUnitInNextRow) {
            result = true;
          }
        } else {
          logger.atInfo().log(
              "This Knight({}) try attack ({}), who outside his radius",
              from.x() + "," + from.y(),
              to.x() + "," + to.y());
          throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
        }
      } else {
        logger.atInfo().log(
            "This {} try attack ally or dead unit", move.getAttacker().getUnitType());
        throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
      }
    }
    if (attacker.getUnitType() == UnitType.ARCHER) {
      if (attackEnemyUnit && isAliveDefender) {
        result = true;
      } else {
        logger.atInfo().log(
            "This {} try attack ally or dead unit", move.getAttacker().getUnitType());
        throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
      }
    }
    if (attacker.getUnitType() == UnitType.MAGE) {
      if (attackEnemyUnit && isAliveDefender) {
        result = true;
      } else {
        logger.atInfo().log(
            "This {} try attack ally or dead unit", move.getAttacker().getUnitType());
        throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
      }
    }
    if (attacker.getUnitType() == UnitType.HEALER) {
      if (!attackEnemyUnit && isAliveDefender) {
        result = true;
      } else {
        logger.atInfo().log(
            "This {} try heal enemy or dead unit", move.getAttacker().getUnitType());
        throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
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

  public static boolean nullUnitNextMeleeRow(
      GameState gameState, Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() + 2) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && gameState.getCurrentBoard().countUnitsRow(from.y() - 2) == 0);
  }

  public static boolean currentPlayerHaveGeneral(Board board, GameState gameState) {
    boolean result = false;
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < Board.ROWS / 2; i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.getUnit(j, i) == null) {
            continue;
          }
          if (board.getUnit(j, i).isAlive() && board.getUnit(j, i).isGeneral()) {
            return result = true;
          }
        }
      }
      return result;
    } else {
      for (int i = 2; i < Board.ROWS; i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.getUnit(j, i) == null) {
            continue;
          }
          if (board.getUnit(j, i).isAlive() && board.getUnit(j, i).isGeneral()) {
            return result = true;
          }
        }
      }
      return result;
    }
  }
}
