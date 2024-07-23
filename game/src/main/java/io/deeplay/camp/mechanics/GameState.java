package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Army;
import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
@Getter
public class GameState {

  private static final Logger logger = LoggerFactory.getLogger(GameLogic.class);

  private Board board;
  private GameStage gameStage;
  private PlayerType currentPlayer;
  private Army armyFirst;
  private Army armySecond;


  public GameState() {
    board = new Board();
    armyFirst = new Army(PlayerType.FIRST_PLAYER);
    armySecond = new Army(PlayerType.SECOND_PLAYER);
    currentPlayer = PlayerType.FIRST_PLAYER;
    gameStage = GameStage.PLACEMENT_STAGE;
  }

  public void changeCurrentPlayer() {
    if (currentPlayer == PlayerType.FIRST_PLAYER) {
      currentPlayer = PlayerType.SECOND_PLAYER;
    } else {
      currentPlayer = PlayerType.FIRST_PLAYER;
    }
  }

  // методы чисто для применения, проверка происходит до их использования
  public void makeMove(MakeMoveEvent move) {
    if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getAttackType()
        == AttackType.MASS_ATTACK) {
      if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getPlayerType()
          == PlayerType.FIRST_PLAYER) {
        for (int i = 0; i < armySecond.getUnits().length; i++) {
          board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(armySecond.getUnits()[i]);
        }
      } else {
        for (int i = 0; i < armyFirst.getUnits().length; i++) {
          board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(armyFirst.getUnits()[i]);
        }
      }
    } else {
      board
          .getUnit(move.getFrom().x(), move.getFrom().y())
          .playMove(board.getUnit(move.getTo().x(), move.getTo().y()));
    }
  }

  public void makePlacement(PlaceUnitEvent placement) throws GameException {

    int x = placement.getColumns();
    int y = placement.getRows();
    boolean result = false;
    logger.atInfo().log("Checking placement for unit {} at ({}, {})", placement.getUnit(), x, y);
    // Проверка стартующая когда расстановка по мнению игрока окончена
    if (!placement.isInProcess()) {
      board.setUnit(x, y, placement.getUnit());
      logger.atInfo().log("Placement process finished. Checking board and general presence.");
      // Проверка на то что на доске есть генерал
      if (getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
        if (!board.isFullFirstPlayerPart()) {
          logger.atError().log("First player board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!currentPlayerHaveGeneral(board, getCurrentPlayer())) {
          logger.atError().log("First player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      } else {
        if (!board.isFullSecondPlayerPart()) {
          logger.atError().log("Second player's board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!currentPlayerHaveGeneral(board, getCurrentPlayer())) {
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
    if (getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
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
  }

  public Board getCurrentBoard() {
    return board;
  }

  public PlayerType getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(PlayerType playerType) {
    this.currentPlayer = playerType;
  }
  private boolean currentPlayerHaveGeneral(Board board,PlayerType playerType) {
    boolean result = false;
    if (playerType == PlayerType.FIRST_PLAYER) {
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
