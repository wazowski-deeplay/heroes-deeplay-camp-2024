package io.deeplay.camp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
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
    game.gameLogic.getGameState().setCurrentPlayer(PlayerType.FIRST_PLAYER);
    game.gameLogic.getGameState().setGameStage(GameStage.MOVEMENT_STAGE);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.SECOND_PLAYER);

    GameException gameException =
        assertThrows(GameException.class, () -> game.changePlayer(changePlayerEvent));
    assertEquals(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE, gameException.getErrorCode());
    assertEquals(PlayerType.FIRST_PLAYER, game.gameLogic.getGameState().getCurrentPlayer());
  }

  @Test
  void testChangePlayerFromCurrentPlayer() {
    game.gameLogic.getGameState().setCurrentPlayer(PlayerType.FIRST_PLAYER);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.FIRST_PLAYER);
    game.gameLogic.getGameState().setGameStage(GameStage.MOVEMENT_STAGE);

    assertDoesNotThrow(() -> game.changePlayer(changePlayerEvent));
    // Если запрос сделал первый игрок, текущий игрок остается первым
    assertEquals(PlayerType.SECOND_PLAYER, game.gameLogic.getGameState().getCurrentPlayer());
  }

  @Test
  void testChangePlayerAtPlacementStage() {
    game.gameLogic.getGameState().setCurrentPlayer(PlayerType.FIRST_PLAYER);
    game.gameLogic.getGameState().setGameStage(GameStage.PLACEMENT_STAGE);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.SECOND_PLAYER);

    GameException gameException =
        assertThrows(GameException.class, () -> game.changePlayer(changePlayerEvent));
    assertEquals(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE, gameException.getErrorCode());
    assertEquals(PlayerType.FIRST_PLAYER, game.gameLogic.getGameState().getCurrentPlayer());
  }
}
