package io.deeplay.camp.manager;

import io.deeplay.camp.GameParty;
import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.PlayerType;
import io.deeplay.camp.player.AiPlayer;
import io.deeplay.camp.player.HumanPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс, отвечающий за менеджмент игровых партий. */
public class GamePartyManager {
  private final Map<UUID, GameParty> gameParties;
  private static final Logger logger = LoggerFactory.getLogger(GamePartyManager.class);

  public GamePartyManager() {
    this.gameParties = new ConcurrentHashMap<>();
  }

  /**
   * Метод, распределяющий запросы на создание игры/подключение к ней.
   *
   * @param clientDto Запрос от клиента.
   */
  public void processCreateOrJoinGameParty(ClientDto clientDto) {
    switch (clientDto.getClientDtoType()) {
      case CREATE_PARTY -> {
        CreateGamePartyDto partyDto = (CreateGamePartyDto) clientDto;
        UUID clientId = partyDto.getClientId();
        GameType gameType = partyDto.getGameType();
        processCreateGameParty(clientId, gameType);
      }
      case JOIN_PARTY -> {
        JoinGamePartyDto joinGamePartyDto = (JoinGamePartyDto) clientDto;
        UUID clientId = joinGamePartyDto.getClientId();
        UUID gamePartyId = joinGamePartyDto.getClientId();
        processJoinParty(gamePartyId, clientId);
      }
      default -> { }
    }
  }

  /**
   * Метод, создающий игру по запросу.
   *
   * @param clientId Id клиента.
   * @param gameType Режим игры.
   */
  public void processCreateGameParty(UUID clientId, GameType gameType) {
    switch (gameType) {
      case HUMAN_VS_BOT -> createHumanVsBotParty(clientId);
      case HUMAN_VS_HUMAN -> createHumanVsHumanParty(clientId);
      default -> { }
    }
  }

  /**
   * Метод, создающий онлайн игру.
   *
   * @param firstHumanPlayerId Id клиента, сделавшего запрос на создание игры.
   */
  private void createHumanVsHumanParty(UUID firstHumanPlayerId) {
    GameParty gameParty = new GameParty(UUID.randomUUID());
    gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, firstHumanPlayerId));
    gameParties.put(gameParty.getGamePartyId(), gameParty);
  }

  /**
   * Метод, создающий игру против бота.
   *
   * @param humanPlayerId Id клиента, сделавшего запрос на создание игры.
   */
  private void createHumanVsBotParty(UUID humanPlayerId) {
    try {
      GameParty gameParty = new GameParty(UUID.randomUUID());
      gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, humanPlayerId));
      gameParty.addPlayer(new AiPlayer(PlayerType.SECOND_PLAYER));
      gameParties.put(gameParty.getGamePartyId(), gameParty);
      gameParty.startGame();
    } catch (Exception e) {
      logger.error("Не удалось создать игру против бота для клиента: {}", humanPlayerId);
    }
  }

  /**
   * Метод обрабатывающий подключение к игре.
   *
   * @param gamePartyId Id игры для подключения.
   * @param clientId Подключающийся клиент.
   */
  public void processJoinParty(UUID gamePartyId, UUID clientId) {
    try {
      GameParty gameParty = gameParties.get(gamePartyId);
      gameParty.addPlayer(new HumanPlayer(PlayerType.SECOND_PLAYER, clientId));
      gameParty.startGame();
    } catch (Exception e) {
      logger.error("Клиенту {} не удалось подключиться к игре {}", clientId, gamePartyId);
    }
  }

  /**
   * Метод, распределяющий запросы между игровыми партиями.
   *
   * @param clientDto Запрос игровой логики.
   */
  public void processGameAction(ClientDto clientDto) throws GameException {
    switch (clientDto.getClientDtoType()) {
      case MAKE_MOVE -> {
        MakeMoveDto makeMoveDto = (MakeMoveDto) clientDto;
        GameParty gameParty = gameParties.get(makeMoveDto.getGamePartyId());
        gameParty.processMakeMove(makeMoveDto);
      }
      case PLACE_UNIT -> {
        PlaceUnitDto placeUnitDto = (PlaceUnitDto) clientDto;
        GameParty gameParty = gameParties.get(placeUnitDto.getGamePartyId());
        gameParty.processPlaceUnit(placeUnitDto);
      }
      case CHANGE_PLAYER -> {
        ChangePlayerDto changePlayerDto = (ChangePlayerDto) clientDto;
        GameParty gameParty = gameParties.get(changePlayerDto.getGamePartyId());
        gameParty.processChangePlayer(changePlayerDto);
      }
      default -> { }
    }
  }
}
