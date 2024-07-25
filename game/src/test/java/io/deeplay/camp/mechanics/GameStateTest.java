package io.deeplay.camp.mechanics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.Healer;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStateTest {

  private GameState gameState;

  @BeforeEach
  public void setUp() {
    gameState = new GameState();
    gameState.getCurrentBoard().setUnit(0, 0, new Archer(PlayerType.FIRST_PLAYER));
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

    gameState.getCurrentBoard().getUnit(0, 2).setNowHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setNowHp(-5);
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
    gameState.getCurrentBoard().getUnit(0, 2).setNowHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setNowHp(-5);
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
    gameState.getCurrentBoard().getUnit(0, 2).setNowHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setNowHp(-5);
    gameState.getCurrentBoard().getUnit(2, 2).setNowHp(-5);
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
    Position posAttacker = new Position(1, 3);
    Position posDefender = new Position(1, 2);
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
}
