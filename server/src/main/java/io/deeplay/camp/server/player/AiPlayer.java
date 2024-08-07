package io.deeplay.camp.server.player;

import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import io.deeplay.camp.server.GameParty;
import io.deeplay.camp.server.bot.Bot;
import io.deeplay.camp.server.bot.RandomBot;
import lombok.Getter;

@Getter
public class AiPlayer extends Player {
  private final Bot bot;
  private final GameParty gameParty;

  public AiPlayer(PlayerType playerType, GameParty gameParty) {
    super(playerType);
    bot = new RandomBot();
    this.gameParty = gameParty;
  }

  @Override
  public void updateGameState(GameStateDto gameStateDto) {
    GameState gameState = gameStateDto.getGameState();
    if (gameState.getCurrentPlayer() == this.getPlayerType()) {
      switch (gameState.getGameStage()) {
        case PLACEMENT_STAGE -> {
          try {
            PlaceUnitEvent place = bot.generatePlaceUnitEvent(gameState);
            if (place == null) {
              gameParty.processChangePlayer(new ChangePlayerDto(gameParty.getGamePartyId()));
            } else {
              PlaceUnitDto placeUnitDto = new PlaceUnitDto(
                      gameParty.getGamePartyId(),
                      place.getColumns(),
                      place.getRows(),
                      place.getUnit().getUnitType(),
                      place.isInProcess(),
                      place.isGeneral());
              placeUnitDto.setClientId(null);
              gameParty.processPlaceUnit(
                  placeUnitDto);
            }
          } catch (GameException e) {
            throw new RuntimeException(e);
          }
        }
        case MOVEMENT_STAGE -> {
          try {
            MakeMoveEvent move = bot.generateMakeMoveEvent(gameState);
            if (move == null) {
              gameParty.processChangePlayer(new ChangePlayerDto(gameParty.getGamePartyId()));
            } else {
              gameParty.processMakeMove(
                  new MakeMoveDto(
                      gameParty.getGamePartyId(),
                      move.getFrom().x(),
                      move.getFrom().y(),
                      move.getTo().x(),
                      move.getTo().y()));
            }
          } catch (GameException e) {
            throw new RuntimeException(e);
          }
        }
        default -> {
          System.out.println("Неопознанное состояние");
        }
      }
    }
  }

  @Override
  public boolean isBotPlayer() {
    return true;
  }
}
