package io.deeplay.camp.mechanics;

import static io.deeplay.camp.mechanics.GameLogic.isValidMove;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamePlayer {
  private static final Logger logger = LoggerFactory.getLogger(GamePlayer.class);

  // Подсчёт количества живых юнитов переданого игрока
  public static List<Position> enumerationPlayerUnits(PlayerType playerType, Board board) {
    List<Position> unitPositions = new ArrayList<>();
    if (playerType == PlayerType.FIRST_PLAYER) {
      for (int i = 0; i < (Board.ROWS / 2); i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.isEmptyCell(j, i)) {
            logger.atInfo().log("Empty cell at ({}, {})", j, i);
            continue;
          }
          if (board.getUnit(j, i).isAlive()) {
            unitPositions.add(new Position(j, i));
          } else {
            logger.atInfo().log("Dead unit at ({}, {})", j, i);
          }
        }
      }
    } else if (playerType == PlayerType.SECOND_PLAYER) {
      for (int i = (Board.ROWS / 2); i < Board.ROWS; i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.isEmptyCell(j, i)) {
            logger.atInfo().log("Empty cell at ({}, {})", j, i);
            continue;
          }
          if (board.getUnit(j, i).isAlive()) {
            unitPositions.add(new Position(j, i));
          } else {
            logger.atInfo().log("Dead unit at ({}, {})", j, i);
          }
        }
      }
    }
    return unitPositions;
  }

  // Возможные варианты действий юнитов. Ключ это какой юнит атакует, значение возможные валидные
  // атаки этого юнита
  public static PossibleActions<Position, Position> unitsPossibleActions(GameState gameState) {
    Board board = gameState.getCurrentBoard();
    PossibleActions<Position, Position> map = new PossibleActions<>();
    // Если тот кто ходить или тот с чей стороны мы хотим узнать возможные ходы
    // Для первого игрока
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      logger.atInfo().log("Calculating possible actions for First Player");
      List<Position> unitsCurrentPlayer = enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
      List<Position> unitsOpponentPlayer = enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);
      for (Position from : unitsCurrentPlayer) {
        // Хилер проходиться не по юнитам противника, а по своим
        if (board.getUnit(from.x(), from.y()).getUnitType() == UnitType.HEALER) {
          for (Position to : unitsCurrentPlayer) {
            MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
            if (canAct(gameState, move)) {
              map.put(from, to);
            } else {
              logger.atInfo().log(
                  "Invalid action for Healer from ({}, {}) to ({}, {})",
                  from.x(),
                  from.y(),
                  to.x(),
                  to.y());
            }
          }
          // Возможные атаки дляG юнитов выбранного игрока по живым юнитам соперника
        } else {
          for (Position to : unitsOpponentPlayer) {
            MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
            if (canAct(gameState, move)) {
              map.put(from, to);
            } else {
              logger.atInfo().log(
                  "Invalid action from ({}, {}) to ({}, {})", from.x(), from.y(), to.x(), to.y());
            }
          }
        }
      }
      // Если тот кто ходить или тот с чей стороны мы хотим узнать возможные ходы
      // Для второго
    } else if (gameState.getCurrentPlayer() == PlayerType.SECOND_PLAYER) {
      logger.atInfo().log("Calculating possible actions for Second Player");
      List<Position> unitsCurrentPlayer = enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);
      List<Position> unitsOpponentPlayer = enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
      for (Position from : unitsCurrentPlayer) {
        // Хилер проходиться не по юнитам противника, а по своим
        if (board.getUnit(from.x(), from.y()).getUnitType() == UnitType.HEALER) {
          for (Position to : unitsCurrentPlayer) {
            MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
            if (canAct(gameState, move)) {
              map.put(from, to);
            } else {
              logger.atInfo().log(
                  "Invalid action for Healer from ({}, {}) to ({}, {})",
                  from.x(),
                  from.y(),
                  to.x(),
                  to.y());
            }
          }
        }
        // Возможные атаки для юнитов выбранного игрока по живым юнитам соперника
        for (Position to : unitsOpponentPlayer) {
          MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
          if (canAct(gameState, move)) {
            map.put(from, to);
          } else {
            logger.atInfo().log(
                "Invalid action from ({}, {}) to ({}, {})", from.x(), from.y(), to.x(), to.y());
          }
        }
      }
    }
    return map;
  }

  public static boolean canAct(GameState gameState, MakeMoveEvent move) {
    boolean result = false;
    try {
      isValidMove(gameState, move);
      result = true;
    } catch (GameException e) {
      logger.atError().log("Move is invalid: {}", e.getMessage());
      result = false;
    }
    return result;
  }
}
