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

public class GameLogicTest {

  private GameState gameState;

  @BeforeEach
  void setUp() {
    gameState = new GameState();
  }

  @Test
  void testBoardIsFull_IsValid() {
    Unit mili = new Knight();
    PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili);
    assertTrue(GameLogic.isValidPlacement(gameState, event));
  }
  @Test
  void testBoardIsFull_IsNotValid() {
    Unit mili = new Knight();
    makeFullBoard();
    PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili);
    assertFalse(GameLogic.isValidPlacement(gameState, event));
  }
  @Test
  void testTakenCell_IsValid() {
    Unit mili = new Knight();
    PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili);
    assertTrue(GameLogic.isValidPlacement(gameState, event));
  }
  @Test
  void testTakenCell_IsNotValid() {
    Unit mili = new Knight();
    gameState.getCurrentBoard().setUnit(1, 1, mili);
    PlaceUnitEvent event = new PlaceUnitEvent(1, 1, mili);
    assertFalse(GameLogic.isValidPlacement(gameState, event));
  }
  @Test
  void testLongAttackUnitPlacedAccordingRules_IsNotValid(){
    Unit reng = new Archer();
    PlaceUnitEvent event = new PlaceUnitEvent(1,1,reng);
    assertFalse(GameLogic.isValidPlacement(gameState,event));
  }

  @Test
  void testLongAttackUnitPlacedAccordingRules_IsValid(){
    Unit reng = new Archer();
    PlaceUnitEvent event = new PlaceUnitEvent(0,0,reng);
    assertTrue(GameLogic.isValidPlacement(gameState,event));
  }

  // ТO DO добавить тест на разных игроков

  private void makeFullBoard() {
    Unit reng = new Archer();
    Unit mili = new Knight();
    for (int i = 0; i < Board.ROWS; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        if (i == 0 || i == 3) {
          gameState.getCurrentBoard().setUnit(i, j, reng);
        } else {
          gameState.getCurrentBoard().setUnit(i, j, mili);
        }
      }
    }
  }
}
