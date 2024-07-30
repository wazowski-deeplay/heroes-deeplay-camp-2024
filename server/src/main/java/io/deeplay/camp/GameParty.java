package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.player.Player;
import io.deeplay.camp.player.Players;
import java.util.UUID;
import lombok.Getter;

/** Класс, отвечающий за конкретную игровую партию. Все запроы транслирует в Game. */
@Getter
public class GameParty {
  private final Game game;
  private final UUID gamePartyId;
  private final Players players;

  public GameParty(UUID gamePartyId) {
    players = new Players();
    this.gamePartyId = gamePartyId;
    game = new Game();
  }

  public void processPlaceUnit(PlaceUnitDto placeUnitDto) throws GameException {
    PlaceUnitEvent placeUnitEvent = DtoToEventConverter.convert(placeUnitDto);
    game.placeUnit(placeUnitEvent);
    updateGameStateForPlayers();
  }

  public void processMakeMove(MakeMoveDto makeMoveRequest) throws GameException {
    MakeMoveEvent makeMoveEvent = DtoToEventConverter.convert(makeMoveRequest);
    game.makeMove(makeMoveEvent);
    updateGameStateForPlayers();
  }

  public void startGame() {}

  public void processChangePlayer(ChangePlayerDto changePlayerRequest) throws GameException {
    ChangePlayerEvent changePlayerEvent = DtoToEventConverter.convert(changePlayerRequest);
    game.changePlayer(changePlayerEvent);
    updateGameStateForPlayers();
  }

  public void addPlayer(Player player) {
    players.addPlayer(player.getPlayerType(), player);
    if (players.isFull()) {
      updateGameStateForPlayers();
    }
  }

  public void updateGameStateForPlayers() {
    GameState gameState = game.getGameState();
    GameStateDto gameStateDto = new GameStateDto(gamePartyId, gameState);
    players.updateGameState(gameStateDto);
  }

  public void close() {}
}
