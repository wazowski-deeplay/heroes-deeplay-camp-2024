package io.deeplay.camp.server;

import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.DrawDto;
import io.deeplay.camp.core.dto.client.game.GiveUpDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.client.game.SwitchPartyDto;
import io.deeplay.camp.game.Game;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.DrawEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.server.exceptions.GameManagerException;
import io.deeplay.camp.server.player.Player;
import io.deeplay.camp.server.player.Players;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс, отвечающий за конкретную игровую партию. Все запроы транслирует в Game. */
@Getter
public class GameParty {
  private static final Logger logger = LoggerFactory.getLogger(Session.class);
  private final Game game;
  private final UUID gamePartyId;
  private final Players players;
  private List<Boolean> draw = new ArrayList<>();

  public GameParty(UUID gamePartyId) {
    players = new Players();
    this.gamePartyId = gamePartyId;
    game = new Game();
    draw.add(false);
    draw.add(false);
  }

  public void processPlaceUnit(PlaceUnitDto placeUnitDto) throws GameException {
    PlaceUnitEvent placeUnitEvent =
        DtoToEventConverter.convert(
            placeUnitDto, players.getPlayerTypeById(placeUnitDto.getClientId()));
    game.placeUnit(placeUnitEvent);
    updateGameStateForPlayers();
  }

  public void processMakeMove(MakeMoveDto makeMoveRequest) throws GameException {
    MakeMoveEvent makeMoveEvent =
        DtoToEventConverter.convert(makeMoveRequest, game.getGameState().getCurrentBoard());
    game.makeMove(makeMoveEvent);
    updateGameStateForPlayers();
  }

  public void processChangePlayer(ChangePlayerDto changePlayerRequest) throws GameException {
    ChangePlayerEvent changePlayerEvent =
        DtoToEventConverter.convert(changePlayerRequest, game.getGameState().getCurrentPlayer());
    game.changePlayer(changePlayerEvent);
    updateGameStateForPlayers();
  }

  public void processGiveUp(GiveUpDto giveUpDto) throws GameException {
    GiveUpEvent giveUpEvent =
        DtoToEventConverter.convert(players.getPlayerTypeById(giveUpDto.getClientId()));
    game.giveUp(giveUpEvent);
    updateGameStateForPlayers();
  }

  public void processSwitchParty(SwitchPartyDto switchPartyDto) {
    updateGameStateForPlayers();
  }

  public void processDraw(List<Boolean> value) throws GameException {
    game.draw(value);
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

  public void setDraw(int index, Boolean value) {
    draw.set(index, value);
  }

  public boolean isGameEnded() {
    return game.getGameState().getGameStage() == GameStage.ENDED;
  }
}
