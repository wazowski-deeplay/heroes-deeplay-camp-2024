package io.deeplay.camp.player;

import io.deeplay.camp.Game;
import io.deeplay.camp.bot.Bot;
import io.deeplay.camp.bot.RandomBot;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.mechanics.PlayerType;

public class AiPlayer extends Player {
  private Bot bot;
  private Game game;

  public AiPlayer(PlayerType playerType, Game game) {
    super(playerType);
    bot = new RandomBot();
    this.game = game;
  }

  @Override
  public void updateGameState(GameStateDto gameStateDto) {
    GameState gameState = gameStateDto.getGameState();
    if (gameState.getCurrentPlayer() == this.getPlayerType()) {
      switch (gameState.getGameStage()) {
        case PLACEMENT_STAGE -> {
          try {
            game.placeUnit(bot.generatePlaceUnitEvent(gameState));
            if (gameState.getCurrentBoard().isFullSecondPlayerPart()) {
              game.changePlayer(new ChangePlayerEvent(this.getPlayerType()));
            }
          } catch (GameException e) {
            throw new RuntimeException(e);
          }
        }
        case MOVEMENT_STAGE -> {
          try {
            game.makeMove(bot.generateMakeMoveEvent(gameState));
            if (gameState.getCurrentBoard().isAllMovedSecondPlayerPart()) {
              game.changePlayer(new ChangePlayerEvent(this.getPlayerType()));
            }
          } catch (GameException e) {
            throw new RuntimeException(e);
          }
        }
        default -> {
          System.out.println("hui");
        }
      }
    }
  }
}
