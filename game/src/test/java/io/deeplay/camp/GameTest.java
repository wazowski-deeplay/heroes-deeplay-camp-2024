package io.deeplay.camp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.BotPlayer;
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
  void testGame() {
    BotPlayer bot1 = new BotPlayer();
    BotPlayer bot2 = new BotPlayer();
    BotFight fight = new BotFight(bot1, bot2, 1, true);
    try {
      fight.playGames();
    } catch (GameException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testChangePlayerRequestFromNotCurrentPlayer() {
    game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
    game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.SECOND_PLAYER);

    GameException gameException =
        assertThrows(GameException.class, () -> game.changePlayer(changePlayerEvent));
    assertEquals(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE, gameException.getErrorCode());
    assertEquals(PlayerType.FIRST_PLAYER, game.gameState.getCurrentPlayer());
  }

  @Test
  void testChangePlayerFromCurrentPlayer() {
    game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.FIRST_PLAYER);
    game.gameState.setGameStage(GameStage.MOVEMENT_STAGE);

    assertDoesNotThrow(() -> game.changePlayer(changePlayerEvent));
    // Если запрос сделал первый игрок, текущий игрок остается первым
    assertEquals(PlayerType.SECOND_PLAYER, game.gameState.getCurrentPlayer());
  }

  @Test
  void testChangePlayerAtPlacementStage() {
    game.gameState.setCurrentPlayer(PlayerType.FIRST_PLAYER);
    game.gameState.setGameStage(GameStage.PLACEMENT_STAGE);
    ChangePlayerEvent changePlayerEvent = new ChangePlayerEvent(PlayerType.SECOND_PLAYER);

    GameException gameException =
        assertThrows(GameException.class, () -> game.changePlayer(changePlayerEvent));
    assertEquals(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE, gameException.getErrorCode());
    assertEquals(PlayerType.FIRST_PLAYER, game.gameState.getCurrentPlayer());
  }
}
