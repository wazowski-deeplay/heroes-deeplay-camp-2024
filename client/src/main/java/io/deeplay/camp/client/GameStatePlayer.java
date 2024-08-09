package io.deeplay.camp.client;

import io.deeplay.camp.client.ui.Cui;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;

import java.util.UUID;

public class GameStatePlayer {
  GameState gameState;
  PlayerType playerTypeInCurrentGame;
  UUID gamePartyId;
  @Getter Cui cui;

  public GameStatePlayer(UUID gamePartyId, PlayerType playerTypeInCurrentGame) {
    cui = new Cui(playerTypeInCurrentGame);
    this.gamePartyId = gamePartyId;
    this.playerTypeInCurrentGame = playerTypeInCurrentGame;
  }

  public void updateBoard(ServerDto serverDto, UUID idCurrent) {
    GameStateDto gameStateDto = (GameStateDto) serverDto;
    gameState = gameStateDto.getGameState();
    cui.updateCui(gameState, gamePartyId, playerTypeInCurrentGame, idCurrent);
  }

  public void cleanBoard(ServerDto serverDto, UUID idCurrent) {
    GamePartyInfoDto gamePartyInfoDto = (GamePartyInfoDto) serverDto;
    playerTypeInCurrentGame = gamePartyInfoDto.getPlayerType();
    gamePartyId = gamePartyInfoDto.getGamePartyId();
    cui.cleanCui(gamePartyId, playerTypeInCurrentGame, idCurrent);
  }

  public void downGameState() {
    cui.downCuiFrame();
    cui = null;
  }
}
