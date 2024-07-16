package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.Healer;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.events.MakeMoveEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    static GameState gameState = new GameState();

    @BeforeAll
    public static void setUp() throws Exception {
        gameState.getBoard().setUnit(0,0,new Archer(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(1,0,new Archer(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(2,0,new Archer(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(0,1,new Knight(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(1,1,new Knight(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(2,1,new Knight(PlayerType.FIRST_PLAYER));
        gameState.getBoard().setUnit(0,2,new Knight(PlayerType.SECOND_PLAYER));
        gameState.getBoard().setUnit(1,2,new Knight(PlayerType.SECOND_PLAYER));
        gameState.getBoard().setUnit(2,2,new Knight(PlayerType.SECOND_PLAYER));
        gameState.getBoard().setUnit(0,3,new Archer(PlayerType.SECOND_PLAYER));
        gameState.getBoard().setUnit(1,3,new Healer(PlayerType.SECOND_PLAYER));
        gameState.getBoard().setUnit(2,3,new Archer(PlayerType.SECOND_PLAYER));
    }
    @Test
    void isValidMoveAttackKnightToKnight() {
        boolean actual = true;
        Position posAttacker = new Position(1,1);
        Position posDefender = new Position(1,2);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveAttackKnightToArcher() {
        boolean actual = false;
        Position posAttacker = new Position(1,1);
        Position posDefender = new Position(2,3);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveAttackArcherToArcher() {
        boolean actual = true;
        Position posAttacker = new Position(1,0);
        Position posDefender = new Position(2,3);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveAttackKnightToFarKnight() {
        boolean actual = false;
        Position posAttacker = new Position(0,1);
        Position posDefender = new Position(2,2);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveAttackKnightToOneKnight() {
        boolean actual = true;
        gameState.getBoard().getUnit(0,2).setNowHp(-5);
        gameState.getBoard().getUnit(1,2).setNowHp(-5);
        Position posAttacker = new Position(0,1);
        Position posDefender = new Position(2,2);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
        gameState.getBoard().getUnit(0,2).setNowHp(5);
        gameState.getBoard().getUnit(1,2).setNowHp(5);
    }
    @Test
    void isValidMoveAttackKnightToArcherWithOneKnight() {
        boolean actual = false;
        gameState.getBoard().getUnit(0,2).setNowHp(-5);
        gameState.getBoard().getUnit(1,2).setNowHp(-5);
        Position posAttacker = new Position(0,1);
        Position posDefender = new Position(2,3);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
        gameState.getBoard().getUnit(0,2).setNowHp(5);
        gameState.getBoard().getUnit(1,2).setNowHp(5);
    }
    @Test
    void isValidMoveAttackKnightToArcherWithNullKnight() {
        boolean actual = true;
        gameState.getBoard().getUnit(0,2).setNowHp(-5);
        gameState.getBoard().getUnit(1,2).setNowHp(-5);
        gameState.getBoard().getUnit(2,2).setNowHp(-5);
        Position posAttacker = new Position(0,1);
        Position posDefender = new Position(2,3);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
        gameState.getBoard().getUnit(0,2).setNowHp(5);
        gameState.getBoard().getUnit(1,2).setNowHp(5);
        gameState.getBoard().getUnit(2,2).setNowHp(5);
    }

    @Test
    void isValidMoveAttackAllyKnightToAllyKnight() {
        boolean actual = false;
        Position posAttacker = new Position(0,1);
        Position posDefender = new Position(1,1);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveHealAllyHealerToAllyKnight() {
        boolean actual = true;
        Position posAttacker = new Position(1,3);
        Position posDefender = new Position(1,2);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
    @Test
    void isValidMoveHealAllyHealerToEnemyKnight() {
        boolean actual = false;
        Position posAttacker = new Position(1,3);
        Position posDefender = new Position(1,1);
        MakeMoveEvent makeMove = new MakeMoveEvent(posAttacker,posDefender,gameState.getBoard().getUnit(posAttacker.x(),posAttacker.y()));
        Assertions.assertEquals(GameLogic.isValidMove(gameState,makeMove),actual);
    }
}