package io.deeplay.camp.player;

import io.deeplay.camp.Game;
import io.deeplay.camp.GameParty;
import io.deeplay.camp.bot.Bot;
import io.deeplay.camp.bot.RandomBot;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.Getter;
@Getter
public class AiPlayer extends Player {
  private Bot bot;
  private GameParty gameParty;

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
              gameParty.processPlaceUnit(
                  new PlaceUnitDto(
                      gameParty.getGamePartyId(),
                      place.getColumns(),
                      place.getRows(),
                      place.getUnit().getUnitType(),
                      place.isInProcess(),
                      place.isGeneral()));
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
          System.out.println("hui");
        }
      }
    }
  }
}
