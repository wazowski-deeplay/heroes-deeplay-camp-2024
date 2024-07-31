package io.deeplay.camp;

import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.dto.server.ServerDto;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.ui.Cui;
import java.util.UUID;

public class GameStatePlayer {
  GameState gameState;
  UUID gamePartyId;
  Cui cui;

  public GameStatePlayer() {
    cui = new Cui();
  }

  public void updateBoard(ServerDto serverDto) {
    GameStateDto gameStateDto = (GameStateDto) serverDto;
    gameState = gameStateDto.getGameState();
    gamePartyId = gameStateDto.getGamePartyId();
    cui.updateCui(gameState.getCurrentBoard());
  }
}
