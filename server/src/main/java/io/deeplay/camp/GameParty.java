package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.GiveUpDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.server.ConnectionErrorCode;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.GiveUpEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.exceptions.GameManagerException;
import io.deeplay.camp.manager.GamePartyManager;
import io.deeplay.camp.mechanics.GameStage;
import io.deeplay.camp.mechanics.GameState;
import io.deeplay.camp.player.Player;
import io.deeplay.camp.player.Players;
import java.util.UUID;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс, отвечающий за конкретную игровую партию. Все запроы транслирует в Game. */
@Getter
public class GameParty {
  private final Game game;
  private final UUID gamePartyId;
  private final Players players;

  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  public GameParty(UUID gamePartyId) {
    players = new Players();
    this.gamePartyId = gamePartyId;
    game = new Game();
  }

  public void processPlaceUnit(PlaceUnitDto placeUnitDto) throws GameException {
    PlaceUnitEvent placeUnitEvent =
        DtoToEventConverter.convert(placeUnitDto, players.getPlayerTypeById(placeUnitDto.getClientId()));
    game.placeUnit(placeUnitEvent);
    updateGameStateForPlayers();
  }

  public void processMakeMove(MakeMoveDto makeMoveRequest) throws GameException {
    MakeMoveEvent makeMoveEvent =
        DtoToEventConverter.convert(makeMoveRequest, game.getGameState().getCurrentBoard());
    game.makeMove(makeMoveEvent);
    updateGameStateForPlayers();
  }

  public void startGame() {}

  public void processChangePlayer(ChangePlayerDto changePlayerRequest) throws GameException {
    ChangePlayerEvent changePlayerEvent =
        DtoToEventConverter.convert(changePlayerRequest, game.getGameState().getCurrentPlayer());
    game.changePlayer(changePlayerEvent);
    updateGameStateForPlayers();
  }

  public void processGiveUp(GiveUpDto giveUpDto) throws GameException{
    GiveUpEvent giveUpEvent = DtoToEventConverter.convert(players.getPlayerTypeById(giveUpDto.getClientId()));
    game.giveUp(giveUpEvent);
    updateGameStateForPlayers();
  }


  public void addPlayer(Player player) throws GameManagerException {
    if (players.isFull()) {
      logger.error("Ошибка добавления игрока, пати переполненна");
      throw new GameManagerException(ConnectionErrorCode.FULL_PARTY);
    }
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

  public boolean isGameEnded() {
    return game.getGameState().getGameStage() == GameStage.ENDED;
  }

  public void close() {}
}
