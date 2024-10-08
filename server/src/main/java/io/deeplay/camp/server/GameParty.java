package io.deeplay.camp.server;

import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.GiveUpDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.core.dto.client.game.SwitchPartyDto;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.game.Game;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.server.exceptions.GameManagerException;
import io.deeplay.camp.server.exceptions.GamePartyException;
import io.deeplay.camp.server.player.AiPlayer;
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
  private Game game;
  private final UUID gamePartyId;
  private final Players players;
  @Setter List<Boolean> restart = new ArrayList<>();
  @Setter List<Boolean> draw = new ArrayList<>();

  public GameParty(UUID gamePartyId) {
    players = new Players();
    this.gamePartyId = gamePartyId;
    game = new Game();
    draw.add(false);
    draw.add(false);
    restart.add(false);
    restart.add(false);
  }

  public void processPlaceUnit(PlaceUnitDto placeUnitDto) throws GamePartyException {
    try {
      int countAi = 0;
      for (Player player : players.getHashMap().values()) {
        if (player instanceof AiPlayer) {
          countAi++;
        }
      }
      if (countAi != 2) {
        PlaceUnitEvent placeUnitEvent =
                DtoToEventConverter.convert(
                        placeUnitDto, players.getPlayerTypeById(placeUnitDto.getClientId()));
        game.placeUnit(placeUnitEvent);
        updateGameStateForPlayers();
      } else {
        PlaceUnitEvent placeUnitEvent =
                DtoToEventConverter.convert(placeUnitDto, game.getGameState().getCurrentPlayer());
        game.placeUnit(placeUnitEvent);
        updateGameStateForPlayers();
      }
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void processMakeMove(MakeMoveDto makeMoveRequest) throws GamePartyException {
    try {
      MakeMoveEvent makeMoveEvent =
              DtoToEventConverter.convert(makeMoveRequest, game.getGameState().getCurrentBoard());
      game.makeMove(makeMoveEvent);
      updateGameStateForPlayers();
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void processChangePlayer(ChangePlayerDto changePlayerRequest) throws GamePartyException {
    try {
      ChangePlayerEvent changePlayerEvent =
              DtoToEventConverter.convert(changePlayerRequest, game.getGameState().getCurrentPlayer());
      game.changePlayer(changePlayerEvent);
      updateGameStateForPlayers();
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void processGiveUp(GiveUpDto giveUpDto) throws GamePartyException {
    try {
      GiveUpEvent giveUpEvent =
              DtoToEventConverter.convert(players.getPlayerTypeById(giveUpDto.getClientId()));
      game.giveUp(giveUpEvent);
      updateGameStateForPlayers();
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void processSwitchParty(SwitchPartyDto switchPartyDto) {
    updateGameStateForPlayers();
  }

  public void processDraw(List<Boolean> value) throws GamePartyException {
    try {
      game.draw(value);
      updateGameStateForPlayers();
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void processRestart(List<Boolean> value) throws GamePartyException {
    try {
      if (value.get(0) && value.get(1)) {
        draw.set(0, false);
        draw.set(1, false);
        game.restartGame();
        game = new Game();
        restart.set(0, false);
        restart.set(1, false);
        updateGameStateForPlayers();
      }
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
  }

  public void closeParty(UUID escapeClient) throws GamePartyException {
    try {
      if (escapeClient != null) {
        GiveUpEvent giveUpEvent = DtoToEventConverter.convert(players.getPlayerTypeById(escapeClient));
        if (players.isFull()) {
          game.giveUp(giveUpEvent);
        } else if (!players.isFull() || game.getGameState().getGameStage() == GameStage.ENDED) {
          game.exitGame(giveUpEvent);
        }
      } else {
        GiveUpEvent giveUpEvent = DtoToEventConverter.convert(game.getGameState().getCurrentPlayer());
        game.exitGame(giveUpEvent);
      }
      game = null;
    } catch (GameException e) {
      throw new GamePartyException(e, gamePartyId);
    }
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

  public void setIndexDraw(int index, Boolean value) {
    draw.set(index, value);
  }

  public void setRestart(int index, Boolean value) {
    restart.set(index, value);
  }

  public boolean isGameEnded() {
    return game.getGameState().getGameStage() == GameStage.ENDED;
  }
}