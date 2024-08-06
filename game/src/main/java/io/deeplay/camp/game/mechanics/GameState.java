package io.deeplay.camp.game.mechanics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.deeplay.camp.game.entities.Army;
import io.deeplay.camp.game.entities.AttackType;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.DrawEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.ErrorCode;
import io.deeplay.camp.game.exceptions.GameException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Setter
@Getter
public class GameState {

  private static final Logger logger = LoggerFactory.getLogger(GameState.class);

  private Board board;
  private GameStage gameStage;
  @Setter @Getter private PlayerType currentPlayer;
  @JsonIgnore private Army armyFirst;
  @JsonIgnore private Army armySecond;
  private int countRound = 10;
  private PlayerType winner;

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
      if (gameStage == GameStage.MOVEMENT_STAGE) {
        armyFirst.updateArmyMoves();
        armySecond.updateArmyMoves();
        countRound--;
      }
      if (gameStage == GameStage.PLACEMENT_STAGE) {
        gameStage = GameStage.MOVEMENT_STAGE;
      }
    }
    if (countRound == 0) {
      winner = winnerOrDraw();
      gameStage = GameStage.ENDED;
    }
  }

  // методы чисто для применения, проверка происходит до их использования
  public void makeMove(MakeMoveEvent move) throws GameException {

    if (gameStage == GameStage.ENDED) {
      throw new GameException(ErrorCode.GAME_IS_OVER);
    }

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
      logger.atInfo().log(
          "This {}({},{}) attack enemy or heal ({},{})",
          move.getAttacker().getUnitType(),
          move.getFrom().x(),
          move.getFrom().y(),
          move.getTo().x(),
          move.getTo().y());
      allUnitsDeadByPlayer();
      armyFirst.isAliveGeneral();
      armySecond.isAliveGeneral();
    }
  }

  public boolean isValidMove(MakeMoveEvent move) throws GameException {
    boolean result = false;
    Position from = move.getFrom();
    Position to = move.getTo();
    Unit attacker = move.getAttacker();

    if (!attacker.isAlive()) {
      logger.atInfo().log(
          "This units {}({},{}) already dead, he wont move",
          move.getAttacker().getUnitType(),
          from.x(),
          from.y());
      throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
    }

    if (outOfBorder(from.x(), from.y()) || outOfBorder(to.x(), to.y())) {
      logger.atInfo().log(
          "These coordinates({},{}) or ({},{}) are outside board border",
          from.x(),
          from.x(),
          to.x(),
          to.y());
      throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
    }

    if (attacker.getPlayerType() != currentPlayer) {
      logger.atInfo().log("Enemy units({},{}) cannot be called to move", from.x(), from.y());
      throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
    }

    if (attacker.getMoved()) {
      logger.atInfo().log(
          "This units {}({},{}) already moved this round",
          move.getAttacker().getUnitType(),
          from.x(),
          from.y());
      throw new GameException(ErrorCode.MOVE_IS_NOT_CORRECT);
    }

    boolean fullUnitInRow = fullUnitMeleeRow(from, to, attacker);
    boolean oneUnitInRow = oneUnitMeleeRow(from, to, attacker);
    boolean nullUnitInRow = nullUnitMeleeRow(from, attacker);
    boolean nullUnitInNextRow = nullUnitNextMeleeRow(from, attacker);
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
              "This Knight({},{}) try attack ({},{}), who outside his radius",
              from.x(),
              from.y(),
              to.x(),
              to.y());
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
    if (gameStage == GameStage.ENDED) {
      throw new GameException(ErrorCode.GAME_IS_OVER);
    }
    if (isValidPlacement(placement)) {
      board.setUnit(placement.getColumns(), placement.getRows(), placement.getUnit());
    }
    if (getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      armyFirst.fillArmy(board);
    } else {
      armySecond.fillArmy(board);
    }
  }

  public boolean isValidPlacement(PlaceUnitEvent placement) throws GameException {
    int x = placement.getColumns();
    int y = placement.getRows();
    logger.atInfo().log("Checking placement for unit {} at ({}, {})", placement.getUnit(), x, y);
    if (placement.getUnit().getPlayerType() != getCurrentPlayer()) {
      logger.error("Not your turn");
      throw new GameException(ErrorCode.NOT_YOUR_TURN);
    }
    if (x > Board.COLUMNS || x < 0) {
      logger.atError().log("Placement coordinates ({}, {}) are out of board bounds.", x, y);
      throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
    }
    if (y > Board.ROWS || y < 0) {
      logger.atError().log("Placement coordinates ({}, {}) are out of board bounds.", x, y);
      throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
    }
    // Проверка на сторону юнита
    if (placement.getUnit().getPlayerType() == PlayerType.FIRST_PLAYER) {
      if (y < (Board.ROWS / 2)) {
        logger.atInfo().log("Placement valid for First Player at ({}, {}).", x, y);
      } else {
        logger.atError().log("Placement invalid for First Player at ({}, {}).", x, y);
        throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
      }
    } else {
      if (y > ((Board.ROWS / 2) - 1) && y < Board.ROWS) {
        logger.atInfo().log("Placement valid for Second Player at ({}, {}).", x, y);
      } else {
        logger.atError().log("Placement invalid for Second Player at ({}, {}).", x, y);
        throw new GameException(ErrorCode.PLACEMENT_INCORRECT);
      }
    }

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
        if (!checkCurrentPlayerGeneral(board, PlayerType.FIRST_PLAYER)) {
          logger.atError().log("First player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      } else {
        if (!board.isFullSecondPlayerPart()) {
          logger.atError().log("Second player's board is not full.");
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
        if (!checkCurrentPlayerGeneral(board, PlayerType.SECOND_PLAYER)) {
          logger.atError().log("Second player general is missing.");
          throw new GameException(ErrorCode.GENERAL_IS_MISSING);
        }
      }
    }
    board.setUnit(x, y, null);
    return true;
  }

  public void makeChangePlayer(ChangePlayerEvent changePlayerEvent) throws GameException {
    if (isValidChangePlayer(changePlayerEvent)) {
      changeCurrentPlayer();
    } else {
      throw new GameException(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE);
    }
  }

  /**
   * Метод проверяет событие перехода хода другому игроку.
   *
   * @param changePlayerEvent Событие передачи хода.
   */
  public boolean isValidChangePlayer(ChangePlayerEvent changePlayerEvent) {
    if (getCurrentPlayer() == changePlayerEvent.getRequester()) {
      logger.atInfo().log("{} has completed his turn", changePlayerEvent.getRequester().name());
      return true;
    } else {
      logger.atInfo().log(
          "{} passes the move out of his turn", changePlayerEvent.getRequester().name());
      return false;
    }
  }

  public Board getCurrentBoard() {
    return board;
  }

  private boolean checkCurrentPlayerGeneral(Board board, PlayerType playerType)
      throws GameException {
    boolean result = false;
    if (playerType == PlayerType.FIRST_PLAYER) {
      int countGeneralFirstPlayer = 0;
      for (int i = 0; i < Board.ROWS / 2; i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.getUnit(j, i) == null) {
            continue;
          }
          if (board.getUnit(j, i).isAlive() && board.getUnit(j, i).isGeneral()) {
            countGeneralFirstPlayer++;
          }
        }
      }
      if (countGeneralFirstPlayer > 1) {
        throw new GameException(ErrorCode.TOO_MANY_GENERAL);
      } else if (countGeneralFirstPlayer == 1) {
        result = true;
      }
      return result;
    } else {
      int countGeneralSecondPlayer = 0;
      for (int i = 2; i < Board.ROWS; i++) {
        for (int j = 0; j < Board.COLUMNS; j++) {
          if (board.getUnit(j, i) == null) {
            continue;
          }
          if (board.getUnit(j, i).isAlive() && board.getUnit(j, i).isGeneral()) {
            countGeneralSecondPlayer++;
          }
        }
      }
      if (countGeneralSecondPlayer > 1) {
        throw new GameException(ErrorCode.TOO_MANY_GENERAL);
      } else if (countGeneralSecondPlayer == 1) {
        result = true;
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

  private boolean nullUnitMeleeRow(Position from, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 1) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 1) == 0);
  }

  private boolean nullUnitNextMeleeRow(Position from, Unit attacker) {
    return (attacker.getPlayerType() == PlayerType.FIRST_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() + 2) == 0)
        || (attacker.getPlayerType() == PlayerType.SECOND_PLAYER
            && getCurrentBoard().countUnitsRow(from.y() - 2) == 0);
  }

  private boolean outOfBorder(int x, int y) {
    return x < 0 || x > Board.COLUMNS - 1 || y < 0 || y > Board.ROWS - 1;
  }

  private void allUnitsDeadByPlayer() {
    if (getCurrentBoard().enumerateUnits(0, Board.ROWS / 2).size() == 0) {
      winner = PlayerType.SECOND_PLAYER;
      gameStage = GameStage.ENDED;
    }
    if (getCurrentBoard().enumerateUnits(Board.ROWS / 2, Board.ROWS).size() == 0) {
      winner = PlayerType.FIRST_PLAYER;
      gameStage = GameStage.ENDED;
    }
  }

  private PlayerType winnerOrDraw() {
    if (board.enumerateUnits(0, Board.ROWS / 2).size()
        > board.enumerateUnits(Board.ROWS / 2, Board.ROWS).size()) {
      return PlayerType.FIRST_PLAYER;
    } else if (board.enumerateUnits(0, Board.ROWS / 2).size()
        < board.enumerateUnits(Board.ROWS / 2, Board.ROWS).size()) {
      return PlayerType.SECOND_PLAYER;
    } else {
      return PlayerType.DRAW;
    }
  }

  public void giveUp(GiveUpEvent giveUpEvent) {
    if (giveUpEvent.getPlayerType() == PlayerType.FIRST_PLAYER) {
      winner = PlayerType.SECOND_PLAYER;
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Победитель - {}, Состояние игры {}", winner, gameStage);
    } else if (giveUpEvent.getPlayerType() == PlayerType.SECOND_PLAYER) {
      winner = PlayerType.FIRST_PLAYER;
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Победитель - {}, Состояние игры {}", winner, gameStage);
    }
  }

  public void draw(List<Boolean> value) {
    if (value.get(0) == true && value.get(1) == true) {
      gameStage = GameStage.ENDED;
      winner = PlayerType.DRAW;
    }
  }

}
