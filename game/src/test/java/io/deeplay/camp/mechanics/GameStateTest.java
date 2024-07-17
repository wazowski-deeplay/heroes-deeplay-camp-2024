package io.deeplay.camp.mechanics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStateTest {

  private GameState gameState;

  @BeforeEach
  public void setUp() {
    gameState = new GameState();
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
}
