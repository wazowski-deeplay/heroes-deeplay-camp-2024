package io.deeplay.camp.client;

import io.deeplay.camp.client.ui.Cui;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import io.deeplay.camp.game.mechanics.GameState;
import java.util.UUID;

public class GameStatePlayer {
  GameState gameState;
  UUID gamePartyId;
  Cui cui;

  public GameStatePlayer(UUID gamePartyId) {
    cui = new Cui();
    this.gamePartyId = gamePartyId;
  }

  public void updateBoard(ServerDto serverDto) {
    GameStateDto gameStateDto = (GameStateDto) serverDto;
    gameState = gameStateDto.getGameState();
    cui.updateCui(gameState.getCurrentBoard(), gamePartyId);

  }
}
