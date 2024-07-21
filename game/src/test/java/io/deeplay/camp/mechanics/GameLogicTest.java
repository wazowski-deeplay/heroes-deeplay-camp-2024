package io.deeplay.camp.mechanics;

import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.Healer;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameLogicTest {

  GameState gameState = new GameState();

  @BeforeEach
  public void setUp() throws Exception {
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
  void isValidMoveAttackKnightToKnight() throws GameException {
    boolean actual = true;
    Position posAttacker = new Position(1, 1);
    Position posDefender = new Position(1, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> GameLogic.isValidMove(gameState, makeMove));
    Assertions.assertEquals(GameLogic.isValidMove(gameState, makeMove), actual);
  }

  @Test
  void isValidMoveAttackKnightToArcher() throws GameException {
    boolean actual = false;
    Position posAttacker = new Position(1, 1);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> GameLogic.isValidMove(gameState, makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackArcherToArcher() throws GameException {
    boolean actual = true;
    Position posAttacker = new Position(1, 0);
    Position posDefender = new Position(2, 3);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    assertDoesNotThrow(() -> GameLogic.isValidMove(gameState, makeMove));
    Assertions.assertEquals(GameLogic.isValidMove(gameState, makeMove), actual);
  }

  @Test
  void isValidMoveAttackKnightToFarKnight() throws GameException {
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> GameLogic.isValidMove(gameState, makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackKnightToOneKnight() throws GameException {
    boolean actual = true;
    gameState.getCurrentBoard().getUnit(0, 2).setNowHp(-5);
    gameState.getCurrentBoard().getUnit(1, 2).setNowHp(-5);
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(2, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    Assertions.assertEquals(GameLogic.isValidMove(gameState, makeMove), actual);
  }

  @Test
  void isValidMoveAttackKnightToArcherWithOneKnight() throws GameException {
    boolean actual = false;
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
        assertThrows(GameException.class, () -> GameLogic.isValidMove(gameState, makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveAttackKnightToArcherWithNullKnight() throws GameException {
    final boolean actual = true;
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
    Assertions.assertEquals(GameLogic.isValidMove(gameState, makeMove), actual);
  }

  @Test
  void isValidMoveAttackAllyKnightToAllyKnight() throws GameException {
    boolean actual = false;
    Position posAttacker = new Position(0, 1);
    Position posDefender = new Position(1, 1);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> GameLogic.isValidMove(gameState, makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }

  @Test
  void isValidMoveHealAllyHealerToAllyKnight() throws GameException {
    boolean actual = true;
    Position posAttacker = new Position(1, 3);
    Position posDefender = new Position(1, 2);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    Assertions.assertEquals(GameLogic.isValidMove(gameState, makeMove), actual);
  }

  @Test
  void isValidMoveHealAllyHealerToEnemyKnight() throws GameException {
    boolean actual = false;
    Position posAttacker = new Position(1, 3);
    Position posDefender = new Position(1, 1);
    MakeMoveEvent makeMove =
        new MakeMoveEvent(
            posAttacker,
            posDefender,
            gameState.getCurrentBoard().getUnit(posAttacker.x(), posAttacker.y()));
    GameException gameException =
        assertThrows(GameException.class, () -> GameLogic.isValidMove(gameState, makeMove));
    assertEquals(ErrorCode.MOVE_IS_NOT_CORRECT, gameException.getErrorCode());
  }
}
