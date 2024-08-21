package io.deeplay.camp.game.mechanics;

import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.ErrorCode;
import io.deeplay.camp.game.exceptions.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GameStateTest {

  private GameState gameState;

  @BeforeEach
  public void setUp() {
    gameState = new GameState();
    gameState.getCurrentBoard().setUnit(0, 0, new Healer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 0, new Archer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(2, 0, new Archer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(0, 1, new Knight(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 1, new Knight(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(2, 1, new Knight(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(0, 2, new Knight(PlayerType.SECOND_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 2, new Knight(PlayerType.SECOND_PLAYER));
    gameState.getCurrentBoard().setUnit(2, 2, new Knight(PlayerType.SECOND_PLAYER));
    gameState.getCurrentBoard().setUnit(0, 3, new Archer(PlayerType.SECOND_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 3, new Healer(PlayerType.SECOND_PLAYER));
    gameState.getCurrentBoard().setUnit(2, 3, new Archer(PlayerType.SECOND_PLAYER));
    gameState.getArmyFirst().fillArmy(gameState.getCurrentBoard());
    gameState.getArmySecond().fillArmy(gameState.getCurrentBoard());
  }

  @Test
  public void testInitialGameState() {
    assertNotNull(gameState);
    assertEquals(PlayerType.FIRST_PLAYER, gameState.getCurrentPlayer());
    assertEquals(GameStage.PLACEMENT_STAGE, gameState.getGameStage());
    assertNotNull(gameState.getBoard());
  }

  @Test
  public void testChangeCurrentPlayer() {
    gameState.changeCurrentPlayer();
    assertEquals(PlayerType.SECOND_PLAYER, gameState.getCurrentPlayer());

    gameState.changeCurrentPlayer();
    assertEquals(PlayerType.FIRST_PLAYER, gameState.getCurrentPlayer());
  }

  @Test
  void isValidMoveAttackKnightToKnight() {
    Position posAttacker = new Position(1, 1);
    Position posDefender = new Position(1, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> gameState.makeMove(makeMove));
  }

  @Test
  void isValidMoveAttackKnightToArcher() {
    Position posAttacker = new Position(1, 1);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makeMove(makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackArcherToArcher() {
    Position posAttacker = new Position(1, 0);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> gameState.makeMove(makeMove));
  }

  @Test
  void isValidMoveAttackKnightToFarKnight() {
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makeMove(makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackKnightToOneKnight() {

    gameState.getCurrentBoard().getUnit(0, 2).setCurrentHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setCurrentHp(-5);
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> gameState.makeMove(makeMove));
  }

  @Test
  void isValidMoveAttackKnightToArcherWithOneKnight() {
    gameState.getCurrentBoard().getUnit(0, 2).setCurrentHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setCurrentHp(-5);
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makeMove(makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackKnightToArcherWithNullKnight() {
    gameState.getCurrentBoard().getUnit(0, 2).setCurrentHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setCurrentHp(-5);
    gameState.getCurrentBoard().getUnit(2, 2).setCurrentHp(-5);
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> gameState.makeMove(makeMove));
  }

  @Test
  void isValidMoveAttackAllyKnightToAllyKnight() {
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(1, 1);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makeMove(makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveHealAllyHealerToAllyKnight() {
    Position posAttacker = new Position(0, 0);
    Position posDefender = new Position(0, 1);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> gameState.makeMove(makeMove));
  }

  @Test
  void isValidMoveHealAllyHealerToEnemyKnight() {
    Position posAttacker = new Position(1, 3);
    Position posDefender = new Position(1, 1);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makeMove(makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void getPossiblePlacesTest() {
    GameState gameState = new GameState();
    List<PlaceUnitEvent> possiblePlaces = gameState.getPossiblePlaces();
    assertEquals(48, possiblePlaces.size());

  }
  @Test
  void getPossiblePlacesLastCellTest() {
    GameState gameState = new GameState();
    gameState.getCurrentBoard().setUnit(0, 0, new Healer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 0, new Archer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(2, 0, new Archer(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(0, 1, new Knight(PlayerType.FIRST_PLAYER));
    gameState.getCurrentBoard().setUnit(1, 1, new Knight(PlayerType.FIRST_PLAYER));
    List<PlaceUnitEvent> possiblePlaces = gameState.getPossiblePlaces();
    assertEquals(4, possiblePlaces.size());
  }

  @Test
  void getPossibleMovesKnightsTest() {
    GameState gameState = new GameState();
    Board board = gameState.getCurrentBoard();
    for(int col = 0; col<Board.COLUMNS; col++){
      for(int row = 0; row<Board.ROWS; row++){
        PlayerType unitOwner = row > 1? PlayerType.SECOND_PLAYER: PlayerType.FIRST_PLAYER;
        board.setUnit(col, row, new Knight(unitOwner));
      }
    }
    gameState.setGameStage(GameStage.MOVEMENT_STAGE);
    List<MakeMoveEvent> possibleMoves = gameState.getPossibleMoves();
    assertEquals(7, possibleMoves.size());
  }

  @Test
  void getPossibleMovesArchersTest() {
    GameState gameState = new GameState();
    Board board = gameState.getCurrentBoard();
    for(int col = 0; col<Board.COLUMNS; col++){
      for(int row = 0; row<Board.ROWS; row++){
        PlayerType unitOwner = row > 1? PlayerType.SECOND_PLAYER: PlayerType.FIRST_PLAYER;
        board.setUnit(col, row, new Archer(unitOwner));
      }
    }
    gameState.setGameStage(GameStage.MOVEMENT_STAGE);
    List<MakeMoveEvent> possibleMoves = gameState.getPossibleMoves();
    assertEquals(36, possibleMoves.size());
  }
  @Test
  public void testGetCopy(){
    GameState copiedGameState = gameState.getCopy();

    assertNotSame(gameState, copiedGameState);

    assertEquals(gameState.getBoard(), copiedGameState.getBoard());
    assertEquals(gameState.getGameStage(), copiedGameState.getGameStage());
    assertEquals(gameState.getCurrentPlayer(), copiedGameState.getCurrentPlayer());
    assertEquals(gameState.getArmyFirst(), copiedGameState.getArmyFirst());
    assertEquals(gameState.getArmySecond(), copiedGameState.getArmySecond());
    assertEquals(gameState.getCountRound(), copiedGameState.getCountRound());
    assertEquals(gameState.getWinner(), copiedGameState.getWinner());

    copiedGameState.setGameStage(GameStage.MOVEMENT_STAGE);
    assertNotEquals(gameState.getGameStage(), copiedGameState.getGameStage());

    copiedGameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
    assertNotEquals(gameState.getCurrentPlayer(), copiedGameState.getCurrentPlayer());

    copiedGameState.setCountRound(5);
    assertNotEquals(gameState.getCountRound(), copiedGameState.getCountRound());

    copiedGameState.setWinner(PlayerType.FIRST_PLAYER);
    assertNotEquals(gameState.getWinner(), copiedGameState.getWinner());
  }
}
