package io.deeplay.camp.client;

import io.deeplay.camp.client.ui.Cui;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import java.util.UUID;

public class GameStatePlayer {
  GameState gameState;
  PlayerType playerTypeInCurrentGame;
  UUID gamePartyId;
  Cui cui;

  public GameStatePlayer(UUID gamePartyId, PlayerType playerTypeInCurrentGame) {
    cui = new Cui(playerTypeInCurrentGame);
    this.gamePartyId = gamePartyId;
    this.playerTypeInCurrentGame = playerTypeInCurrentGame;
  }

  public void updateBoard(ServerDto serverDto) {
    GameStateDto gameStateDto = (GameStateDto) serverDto;
    gameState = gameStateDto.getGameState();
    cui.updateCui(gameState, gamePartyId);
  }

  public void downGameState() {
    cui.downCuiFrame();
    cui = null;
  }
}
