package io.deeplay.camp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.mechanics.GameStage;
import io.deeplay.camp.mechanics.PlayerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {
  private Game game;

  @BeforeEach
  public void setUp() {
    game = new Game();
  }

  @Test
  void testChangePlayerRequestFromNotCurrentPlayer() {
    game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
    game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.SECOND_PLAYER);
    game.changePlayer(changePlayerEvent);

    //Если запрос сделал второй игрок, текущий не должен поменяться
    assertEquals(game.gameState.getCurrentPlayer(), PlayerType.FIRST_PLAYER);
  }

  @Test
  void testChangePlayerFromCurrentPlayer() {
    game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.FIRST_PLAYER);
    game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);
    game.changePlayer(changePlayerEvent);

    //Если запрос сделал первый игрок, текущий не должен поменяться
    assertEquals(game.gameState.getCurrentPlayer(), PlayerType.SECOND_PLAYER);
  }

}
