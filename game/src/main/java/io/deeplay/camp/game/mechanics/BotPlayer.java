package io.deeplay.camp.game.mechanics;

import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlayer {
  private static final Logger logger = LoggerFactory.getLogger(BotPlayer.class);

  public List<Unit> enumerationUnit(PlayerType playerType) {
    List<Unit> tmp = new ArrayList<>();
    tmp.add(new Knight(playerType));
    tmp.add(new Archer(playerType));
    tmp.add(new Healer(playerType));
    tmp.add(new Mage(playerType));
    return tmp;
  }

  // Подсчёт количества живых юнитов переданого игрока
  public List<Position> enumerationPlayerUnits(PlayerType playerType, Board board) {
    List<Position> unitPositions = new ArrayList<>();
    if (playerType == PlayerType.FIRST_PLAYER) {
      unitPositions.addAll(board.enumerateUnits(0, Board.ROWS / 2));
    } else {
      unitPositions.addAll(board.enumerateUnits(Board.ROWS / 2, Board.ROWS));
    }
    return unitPositions;
  }

  // Подсчёт количества пустых клеток у выбранного игрока
  public List<Position> enumerationEmptyCells(PlayerType playerType, Board board) {
    List<Position> unitPositions = new ArrayList<>();
    if (playerType == PlayerType.FIRST_PLAYER) {
      unitPositions.addAll(board.enumerateEmptyCells(0, Board.ROWS / 2));
    } else {
      unitPositions.addAll(board.enumerateEmptyCells(Board.ROWS / 2, Board.ROWS));
    }
    return unitPositions;
  }

  // Возможные варианты действий юнитов
  // Ключ это какой юнит атакует
  // значение возможные валидные атаки этого юнита
  public PossibleActions<Position, Position> unitsPossibleActions(GameState gameState) {
    Board board = gameState.getCurrentBoard();
    PossibleActions<Position, Position> map = new PossibleActions<>();
    List<Position> unitsCurrentPlayer;
    List<Position> unitsOpponentPlayer;
    // Ключ - это атакующий юнит, значение - это все возможные атаки данного юнита
    // Для первого игрока
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      logger.atInfo().log("Calculating possible actions for First Player");
      unitsCurrentPlayer = enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
      unitsOpponentPlayer = enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);
      for (Position from : unitsCurrentPlayer) {
        // Хилер проходиться не по юнитам противника, а по своим
        if (board.getUnit(from.x(), from.y()).getUnitType() == UnitType.HEALER) {
          if (!board.getUnit(from.x(), from.y()).getMoved()) {
            for (Position to : unitsCurrentPlayer) {
              MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
              if (canActMove(gameState, move)) {
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
        } else {
          if (!board.getUnit(from.x(), from.y()).getMoved()) {
            for (Position to : unitsOpponentPlayer) {
              MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
              if (canActMove(gameState, move)) {
                map.put(from, to);
              } else {
                logger.atInfo().log(
                    "Invalid action from ({}, {}) to ({}, {})", from.x(), from.y(), to.x(), to.y());
              }
            }
          }
        }
      }
      // Если тот кто ходить или тот с чей стороны мы хотим узнать возможные ходы
      // Для второго
    } else if (gameState.getCurrentPlayer() == PlayerType.SECOND_PLAYER) {
      logger.atInfo().log("Calculating possible actions for Second Player");
      unitsCurrentPlayer = enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);
      unitsOpponentPlayer = enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
      for (Position from : unitsCurrentPlayer) {
        // Хилер проходиться не по юнитам противника, а по своим
        if (board.getUnit(from.x(), from.y()).getUnitType() == UnitType.HEALER) {
          for (Position to : unitsCurrentPlayer) {
            MakeMoveEvent move = new MakeMoveEvent(from, to, board.getUnit(from.x(), from.y()));
            if (canActMove(gameState, move)) {
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
          if (canActMove(gameState, move)) {
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

  // Возможные варианты расстановки юнитов
  // Ключ - какая клетка рассматривается для размещения
  // Значение - возможные юниты для расстановки
  public PossibleActions<Position, Unit> unitsPossiblePlacement(GameState gameState) {
    Board board = gameState.getCurrentBoard();
    PossibleActions<Position, Unit> map = new PossibleActions<>();
    List<Position> cellsCurrentPlayer;
    List<Unit> unitList;
    // Ключ - какая клетка рассматривается для размещения, значение - возможные юниты для
    // расстановки
    // Для первого игрока
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      logger.atInfo().log("Calculating possible placement for First Player");
      cellsCurrentPlayer = enumerationEmptyCells(PlayerType.FIRST_PLAYER, board);
      unitList = enumerationUnit(PlayerType.FIRST_PLAYER);
      for (Position to : cellsCurrentPlayer) {
        if (board.isEmptyCell(to.x(), to.y())) {
          boolean inProcess = true;
          boolean general = false;
          if (enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board).size() + 1 == 6) {
            inProcess = false;
            general = true;
          }
          for (Unit randUnit : unitList) {
            PlaceUnitEvent place =
                new PlaceUnitEvent(
                    to.x(), to.y(), randUnit, PlayerType.FIRST_PLAYER, inProcess, general);
            if (canActPlace(gameState, place)) {
              map.put(to, randUnit);
            } else {
              logger.atInfo().log(
                  "Invalid placement for First Player from ({}, {}) for {})",
                  to.x(),
                  to.y(),
                  randUnit.getUnitType().name());
            }
          }
        }
      }
      // Для второго
    } else if (gameState.getCurrentPlayer() == PlayerType.SECOND_PLAYER) {
      logger.atInfo().log("Calculating possible placement for Second Player");
      cellsCurrentPlayer = enumerationEmptyCells(PlayerType.SECOND_PLAYER, board);
      unitList = enumerationUnit(PlayerType.SECOND_PLAYER);
      for (Position to : cellsCurrentPlayer) {
        if (board.isEmptyCell(to.x(), to.y())) {
          boolean inProcess = true;
          boolean general = false;
          if (enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board).size() + 1 == 6) {
            inProcess = false;
            general = true;
          }
          for (Unit randUnit : unitList) {
            PlaceUnitEvent place =
                new PlaceUnitEvent(
                    to.x(), to.y(), randUnit, PlayerType.SECOND_PLAYER, inProcess, general);
            if (canActPlace(gameState, place)) {
              map.put(to, randUnit);
            } else {
              logger.atInfo().log(
                  "Invalid placement for Second Player from ({}, {}) for {})",
                  to.x(),
                  to.y(),
                  randUnit.getUnitType().name());
            }
          }
        }
      }
    }
    return map;
  }

  // Метод обёртка для отлавливания ошибок для действий юнитов
  private boolean canActMove(GameState gameState, MakeMoveEvent move) {
    boolean result;
    try {
      gameState.isValidMove(move);
      result = true;
    } catch (GameException e) {
      logger.atError().log("Move is invalid: {}", e.getMessage());
      result = false;
    }
    return result;
  }

  // Метод обёртка для отлавливания ошибок для расстановки юнитов
  private boolean canActPlace(GameState gameState, PlaceUnitEvent placeUnitEvent) {
    boolean result;
    try {
      gameState.isValidPlacement(placeUnitEvent);
      result = true;
    } catch (GameException e) {
      logger.atError().log("Place is invalid: {}", e.getMessage());
      result = false;
    }
    return result;
  }
}
