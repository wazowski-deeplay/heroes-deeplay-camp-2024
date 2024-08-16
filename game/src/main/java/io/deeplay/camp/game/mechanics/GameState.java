package io.deeplay.camp.game.mechanics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Army;
import io.deeplay.camp.game.entities.AttackInfo;
import io.deeplay.camp.game.entities.AttackType;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Defender;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.ErrorCode;
import io.deeplay.camp.game.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public GameState(GameState gameState) {
    this.board = new Board(gameState.board);
    this.gameStage = gameState.gameStage;
    this.currentPlayer = gameState.currentPlayer;
    this.armyFirst = new Army(gameState.armyFirst, this.board);
    this.armySecond = new Army(gameState.armySecond, this.board);
    this.countRound = gameState.countRound;
    this.winner = gameState.winner;
  }

  public void changeCurrentPlayer() {
    if (currentPlayer == PlayerType.FIRST_PLAYER) {
      currentPlayer = PlayerType.SECOND_PLAYER;
    } else {
      currentPlayer = PlayerType.FIRST_PLAYER;
      if (gameStage == GameStage.MOVEMENT_STAGE) {
        armyFirst.updateArmy();
        armySecond.updateArmy();
        countRound--;
      }
      if (gameStage == GameStage.PLACEMENT_STAGE) {
        gameStage = GameStage.MOVEMENT_STAGE;
      }
    }
    if (countRound == 0) {
      winner = winnerOrDraw();
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Result {}, game is {}", winner, gameStage);
    }
  }

  public List<AttackInfo> makeMove(MakeMoveEvent move) throws GameException {
    List<AttackInfo> attackResult = new ArrayList<>();
    if (isValidMove(move)) {
      Unit attacker = board.getUnit(move.getFrom().x(), move.getFrom().y());
      List<Defender> defenders = new ArrayList<>();

      if (attacker.getAttackType() == AttackType.MASS_ATTACK) {
        if (attacker.getPlayerType() == PlayerType.FIRST_PLAYER) {
          for (Unit defender : armySecond.getUnits()) {
            attacker.playMove(defender);
            if (attacker.isHitTarget()) {
              Defender unitDefender = new Defender(defender, true);
              defenders.add(unitDefender);
            } else {
              Defender unitDefender = new Defender(defender, false);
              defenders.add(unitDefender);
            }
          }
        } else {
          for (Unit defender : armyFirst.getUnits()) {
            attacker.playMove(defender);
            if (attacker.isHitTarget()) {
              Defender unitDefender = new Defender(defender, true);
              defenders.add(unitDefender);
            } else {
              Defender unitDefender = new Defender(defender, false);
              defenders.add(unitDefender);
            }
          }
        }
        attackResult.add(new AttackInfo(attacker, defenders));
      } else {
        Unit defender = board.getUnit(move.getTo().x(), move.getTo().y());
        attacker.playMove(defender);
        if (attacker.isHitTarget()) {
          Defender unitDefender = new Defender(defender, true);
          defenders.add(unitDefender);
        } else {
          Defender unitDefender = new Defender(defender, false);
          defenders.add(unitDefender);
        }
        attackResult.add(new AttackInfo(attacker, defenders));
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
    return attackResult;
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

    if (attacker.isMoved()) {
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

    logger.atInfo().log(
        "Checking placement for unit {} at ({}, {}) {}",
        placement.getUnit(),
        x,
        y,
        placement.getUnit().getPlayerType());
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
    if (changePlayerEvent.getRequester() != currentPlayer) {
      throw new GameException(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE);
    }
    if (gameStage == GameStage.PLACEMENT_STAGE) {
      if (changePlayerEvent.getRequester() == PlayerType.FIRST_PLAYER) {
        if (!board.isFullFirstPlayerPart()) {
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
      } else if (changePlayerEvent.getRequester() == PlayerType.SECOND_PLAYER) {
        if (!board.isFullSecondPlayerPart()) {
          throw new GameException(ErrorCode.BOARD_IS_NOT_FULL);
        }
      } else {
        throw new GameException(ErrorCode.UNDEFINED_ERROR);
      }
    }
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
      logger.atInfo().log("Result {}, is {}", winner, gameStage);
    }
    if (getCurrentBoard().enumerateUnits(Board.ROWS / 2, Board.ROWS).size() == 0) {
      winner = PlayerType.FIRST_PLAYER;
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Result {}, is {}", winner, gameStage);
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

  public void exitGame(GiveUpEvent giveUpEvent) {
    if (giveUpEvent.getPlayerType() == PlayerType.FIRST_PLAYER) {
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Первый игрок покинул свою игру");
    } else if (giveUpEvent.getPlayerType() == PlayerType.SECOND_PLAYER) {
      gameStage = GameStage.ENDED;
      logger.atInfo().log("Второй игрок покинул свою игру");
    }
  }

  public void draw(List<Boolean> value) {
    if (value.get(0) && value.get(1)) {
      gameStage = GameStage.ENDED;
      winner = PlayerType.DRAW;
    }
  }

  public void restartGame() {
    board = null;
    armyFirst = null;
    armySecond = null;
    currentPlayer = null;
    gameStage = null;
  }

  public GameState getCopy() {
    return new GameState(this);
  }

  public List<PlaceUnitEvent> getPossiblePlaces() {
    PlayerType currentPlayer = getCurrentPlayer();
    Army army = currentPlayer == PlayerType.FIRST_PLAYER ? getArmyFirst() : getArmySecond();
    Board board = getBoard();
    int startRow = currentPlayer == PlayerType.FIRST_PLAYER ? 0 : Board.ROWS / 2;
    int endRow = currentPlayer == PlayerType.FIRST_PLAYER ? Board.ROWS / 2 : Board.ROWS;

    List<PlaceUnitEvent> possiblePlaces = new ArrayList<>();
    for (int col = 0; col < Board.COLUMNS; col++) {
      for (int row = startRow; row < endRow; row++) {
        if (board.isEmptyCell(col, row)) {
          boolean isLastEmptyCell = board.hasOneEmptyCell(currentPlayer);
          for (UnitType unitType : UnitType.values()) {
            if (!isLastEmptyCell || army.hasGeneral()) {
              PlaceUnitEvent placeUnitEvent = new PlaceUnitEvent(
                      col,
                      row,
                      Unit.createUnitByUnitType(unitType, currentPlayer),
                      currentPlayer,
                      !isLastEmptyCell,
                      false);
              possiblePlaces.add(placeUnitEvent);
            }

            if (!army.hasGeneral()) {
              PlaceUnitEvent placeUnitEventWithGeneral =
                  new PlaceUnitEvent(
                      col,
                      row,
                      Unit.createUnitByUnitType(unitType, currentPlayer),
                      currentPlayer,
                      !isLastEmptyCell,
                      true);
              possiblePlaces.add(placeUnitEventWithGeneral);
            }
          }
        }
      }
    }
    return possiblePlaces;
  }

  public List<MakeMoveEvent> getPossibleMoves() {
    Board board = getCurrentBoard();
    List<MakeMoveEvent> possibleMoves = new ArrayList<>();
    List<Position> unitsPositionsCurrentPlayer;
    List<Position> unitsPositionsOpponentPlayer;
    PlayerType currentPlayer = getCurrentPlayer();
    PlayerType opponentPlayer =
        currentPlayer == PlayerType.FIRST_PLAYER
            ? PlayerType.SECOND_PLAYER
            : PlayerType.FIRST_PLAYER;

    unitsPositionsCurrentPlayer = collectPositionsOfPlayer(currentPlayer, board);
    unitsPositionsOpponentPlayer = collectPositionsOfPlayer(opponentPlayer, board);

    for (Position from : unitsPositionsCurrentPlayer) {
      Unit unit = board.getUnit(from.x(), from.y());
      if (unit.getUnitType() == UnitType.HEALER) {
        addValidMoves(possibleMoves, unitsPositionsCurrentPlayer, from, unit);
      } else {
        addValidMoves(possibleMoves, unitsPositionsOpponentPlayer, from, unit);
      }
    }

    return possibleMoves;
  }

  private void addValidMoves(
      List<MakeMoveEvent> possibleMoves, List<Position> targetPositions, Position from, Unit unit) {
    for (Position to : targetPositions) {
      if (!unit.isMoved()) {
        MakeMoveEvent move = new MakeMoveEvent(from, to, unit);
        if (canActMove(move)) {
          possibleMoves.add(move);
        }
      }
    }
  }

  private boolean canActMove(MakeMoveEvent move) {
    try {
      isValidMove(move);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<Position> collectPositionsOfPlayer(PlayerType playerType, Board board) {
    List<Position> unitPositions = new ArrayList<>();
    if (playerType == PlayerType.FIRST_PLAYER) {
      unitPositions.addAll(board.enumerateUnits(0, Board.ROWS / 2));
    } else {
      unitPositions.addAll(board.enumerateUnits(Board.ROWS / 2, Board.ROWS));
    }
    return unitPositions;
  }

  public void setDefaultPlacement() {
    try{
      Knight generalKnight = new Knight(PlayerType.FIRST_PLAYER);
      makePlacement(new PlaceUnitEvent(0, 0, generalKnight, PlayerType.FIRST_PLAYER, true, true));
      makePlacement(new PlaceUnitEvent(1, 0, new Mage(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(2, 0, new Healer(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(0, 1, new Archer(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(1, 1, new Knight(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(2, 1, new Knight(PlayerType.FIRST_PLAYER), PlayerType.FIRST_PLAYER, false, false));
      makeChangePlayer(new ChangePlayerEvent(PlayerType.FIRST_PLAYER));

      generalKnight = new Knight(PlayerType.SECOND_PLAYER);
      makePlacement(new PlaceUnitEvent(2, 3, generalKnight, PlayerType.SECOND_PLAYER, true, true));
      makePlacement(new PlaceUnitEvent(1, 3, new Mage(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(0, 3, new Healer(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(2, 2, new Archer(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(1, 2, new Knight(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, true, false));
      makePlacement(new PlaceUnitEvent(0, 2, new Knight(PlayerType.SECOND_PLAYER), PlayerType.SECOND_PLAYER, false, false));
      makeChangePlayer(new ChangePlayerEvent(PlayerType.SECOND_PLAYER));
    }catch (GameException e){
      logger.info("Не удалась стандартная расстановка!");
    }
  }

}
