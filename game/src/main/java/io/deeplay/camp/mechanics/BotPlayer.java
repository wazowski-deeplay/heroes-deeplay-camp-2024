package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotPlayer implements GamePlayer {
  private static final Logger logger = LoggerFactory.getLogger(BotPlayer.class);

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

  // Возможные варианты действий юнитов
  // Ключ это какой юнит атакует
  // значение возможные валидные атаки этого юнита
  public PossibleActions<Position, Position> unitsPossibleActions(GameState gameState) {
    Board board = gameState.getCurrentBoard();
    PossibleActions<Position, Position> map = new PossibleActions<>();
    List<Position> unitsCurrentPlayer = new ArrayList<>();
    List<Position> unitsOpponentPlayer = new ArrayList<>();
    // Ключ - это атакующий юнит, значение - это все возможные атаки данного юнита
    // Для первого игрока
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      logger.atInfo().log("Calculating possible actions for First Player");
      unitsCurrentPlayer = enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
      unitsOpponentPlayer = enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);
      for (Position from : unitsCurrentPlayer) {
        // Хилер проходиться не по юнитам противника, а по своим
        if (board.getUnit(from.x(), from.y()).getUnitType() == UnitType.HEALER) {
          if(!board.getUnit(from.x(), from.y()).getMoved()){
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
        } else {
          if(!board.getUnit(from.x(), from.y()).getMoved()){
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

  private boolean canAct(GameState gameState, MakeMoveEvent move) {
    boolean result = false;
    try {
      gameState.isValidMove(move);
      result = true;
    } catch (GameException e) {
      logger.atError().log("Move is invalid: {}", e.getMessage());
      result = false;
    }
    return result;
  }

  @Override
  public void startGame(StartGameEvent event) {}

  @Override
  public void placeUnit(PlaceUnitEvent event) throws GameException {}

  @Override
  public void changePlayer(ChangePlayerEvent event) throws GameException {}

  @Override
  public void makeMove(MakeMoveEvent event) throws GameException {}
}
