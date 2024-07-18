package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.events.PlaceUnitEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameLogicPlacementTest {

    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
    }

    @Test
    void testBoardIsFull_IsValid() {
        Unit mili = new Knight(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili,gameState.getCurrentPlayer());
        assertTrue(GameLogic.isValidPlacement(gameState, event));
    }
    @Test
    void testBoardIsFull_IsNotValid() {
        Unit mili = new Knight(gameState.getCurrentPlayer());
        makeFullBoard();
        PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili,gameState.getCurrentPlayer());
        assertFalse(GameLogic.isValidPlacement(gameState, event));
    }
    @Test
    void testTakenCell_IsValid() {
        Unit mili = new Knight(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili,gameState.getCurrentPlayer());
        assertTrue(GameLogic.isValidPlacement(gameState, event));
    }
    @Test
    void testTakenCell_IsNotValid() {
        Unit mili = new Knight(gameState.getCurrentPlayer());
        gameState.getCurrentBoard().setUnit(1, 1, mili);
        PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili,gameState.getCurrentPlayer());
        assertFalse(GameLogic.isValidPlacement(gameState, event));
    }
    @Test
    void testCorrectSideFirstPlayer_IsNotValid(){
        Unit mili = new Knight(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(2,2,mili,gameState.getCurrentPlayer());
        assertFalse(GameLogic.isValidPlacement(gameState,event));
    }
    @Test
    void testCorrectSideFirstPlayer_IsValid(){
        Unit reng = new Archer(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(1,1,reng,gameState.getCurrentPlayer());
        assertTrue(GameLogic.isValidPlacement(gameState,event));
    }
    @Test
    void testCorrectSideSecondPlayer_IsNotValid(){
        gameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
        Unit reng = new Knight(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(1,1,reng,gameState.getCurrentPlayer());
        assertFalse(GameLogic.isValidPlacement(gameState,event));
    }
    @Test
    void testCorrectSideSecondPlayer_IsValid(){
        gameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
        Unit reng = new Knight(gameState.getCurrentPlayer());
        PlaceUnitEvent event = new PlaceUnitEvent(0,2,reng,gameState.getCurrentPlayer());
        assertTrue(GameLogic.isValidPlacement(gameState,event));
    }



    private void makeFullBoard() {
        Unit reng = new Archer(gameState.getCurrentPlayer());
        Unit mili = new Knight(gameState.getCurrentPlayer());
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                if (i == 0 || i == 3) {
                    gameState.getCurrentBoard().setUnit(i, j, reng);
                } else {
                    gameState.getCurrentBoard().setUnit(i, j, mili);
                }
            }
        }
    }
}
