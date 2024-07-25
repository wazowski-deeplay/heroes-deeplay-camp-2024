package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Army;
import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.entities.UnitType;
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
  public void makeMove(MakeMoveEvent move) throws GameException {
    if (isValidMove(move)) {
      if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getAttackType()
          == AttackType.MASS_ATTACK) {
        if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getPlayerType()
            == PlayerType.FIRST_PLAYER) {
          for (int i = 0; i < armySecond.getUnits().length; i++) {
            board
                .getUnit(move.getFrom().x(), move.getFrom().y())
                .playMove(armySecond.getUnits()[i]);
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
  }

  public boolean isValidMove(MakeMoveEvent move) throws GameException {
    boolean result = false;
    Position from = move.getFrom();
    Position to = move.getTo();
    Unit attacker = move.getAttacker();

    boolean fullUnitInRow = fullUnitMeleeRow(from, to, attacker);
    boolean oneUnitInRow = oneUnitMeleeRow(from, to, attacker);
    boolean nullUnitInRow = nullUnitMeleeRow(from, to, attacker);
    boolean nullUnitInNextRow = nullUnitNextMeleeRow(from, to, attacker);
    boolean attackEnemyUnit =
        getCurrentBoard().getUnit(to.x(), to.y()).getPlayerType() != attacker.getPlayerType();
    boolean isAliveDefender = getCurrentBoard().getUnit(to.x(), to.y()).isAlive();

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

  public void makePlacement(PlaceUnitEvent placement) throws GameException {
    if (isValidPlacement(placement)) {
      board.setUnit(placement.getColumns(), placement.getRows(), placement.getUnit());
    }
    if (getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      armyFirst.fillArmy(board);
    } else {
      armySecond.fillArmy(board);
    }
  }

  private boolean isValidPlacement(PlaceUnitEvent placement) throws GameException {
    int x = placement.getColumns();
    int y = placement.getRows();
    boolean result = false;
    logger.atInfo().log("Checking placement for unit {} at ({}, {})", placement.getUnit(), x, y);

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

    // Если ход игрока корректен с точки зрения кординатов применяем ход и проверяем на корректность
    // правил
    board.setUnit(x, y, placement.getUnit());

    // Проверка стартующая когда расстановка по мнению игрока окончена
    if (!placement.isInProcess()) {
      logger.atInfo().log("Placement process finished. Checking board and general presence.");
      // Проверка на то что на доске есть генерал
      if (getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
        if (!board.isFullFirstPlayerPart()) {
          logger.atError().log("First player board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!currentPlayerHaveGeneral(board, PlayerType.FIRST_PLAYER)) {
          logger.atError().log("First player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      } else {
        if (!board.isFullSecondPlayerPart()) {
          logger.atError().log("Second player's board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!currentPlayerHaveGeneral(board, PlayerType.SECOND_PLAYER)) {
          logger.atError().log("Second player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      }
      result = true;
    }
    return result;
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

  private boolean currentPlayerHaveGeneral(Board board, PlayerType playerType) {
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

  private boolean fullUnitMeleeRow(Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 1) > 1
            && to.y() == from.y() + 1)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 1) > 1
            && to.y() == from.y() - 1);
  }

  private boolean oneUnitMeleeRow(Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 1) == 1
            && to.y() == from.y() + 1)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 1) == 1
            && to.y() == from.y() - 1);
  }

  private boolean nullUnitMeleeRow(Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 1) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 1) == 0);
  }

  private boolean nullUnitNextMeleeRow(Position from, Position to, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 2) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 2) == 0);
  }
}
