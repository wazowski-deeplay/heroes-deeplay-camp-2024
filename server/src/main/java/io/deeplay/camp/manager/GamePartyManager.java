package io.deeplay.camp.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.GameParty;
import io.deeplay.camp.JsonConverter;
import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.dto.server.GamePartyInfoDto;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.PlayerType;
import io.deeplay.camp.player.AiPlayer;
import io.deeplay.camp.player.HumanPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamePartyManager {
  private final Map<UUID, GameParty> gameParties;
  private static final Logger logger = LoggerFactory.getLogger(GamePartyManager.class);

  public GamePartyManager() {
    this.gameParties = new ConcurrentHashMap<>();
  }

  /**
   * Метод, распределяющий запросы на создание/подключение к пати.
   *
   * @param clientDto Запрос клиента.
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
        UUID gamePartyId = joinGamePartyDto.getGamePartyId();
        processJoinParty(gamePartyId, clientId);
      }
      default -> { }
    }
  }

  /**
   * Метод обаботки создания игры.
   *
   * @param clientId Id клиента, сделавшего запрос.
   * @param gameType Тип игры.
   */
  public void processCreateGameParty(UUID clientId, GameType gameType) {
    switch (gameType) {
      case HUMAN_VS_BOT -> createHumanVsBotParty(clientId);
      case HUMAN_VS_HUMAN -> createHumanVsHumanParty(clientId);
      default -> { }
    }
  }

  /**
   * Метод создания онлайн игры. Создаёт пати и сразу добавляет клиента как игрока. Затем отправляет
   * ему информацию о пати.
   *
   * @param firstHumanPlayerId Id клиента, запросившего создание.
   */
  private void createHumanVsHumanParty(UUID firstHumanPlayerId) {
    GameParty gameParty = new GameParty(UUID.randomUUID());
    gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, firstHumanPlayerId));
    gameParties.put(gameParty.getGamePartyId(), gameParty);

    GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
    sendGamePartyInfo(firstHumanPlayerId, gamePartyInfoDto);
  }

  /**
   * Метод создания игры с ботом. Создаёт пати и сразу добавляет клиента и бота как игроков. Затем
   * отправляется инф. о пати клиенту.
   *
   * @param humanPlayerId Id клиента, запросившего создание.
   */
  private void createHumanVsBotParty(UUID humanPlayerId) {
    GameParty gameParty = new GameParty(UUID.randomUUID());
    gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, humanPlayerId));
    gameParties.put(gameParty.getGamePartyId(), gameParty);

    GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
    sendGamePartyInfo(humanPlayerId, gamePartyInfoDto);

    gameParty.addPlayer(new AiPlayer(PlayerType.SECOND_PLAYER));
  }

  /**
   * Метод обработки подключения к пати. Добавляет игрока в пати, если она не полная.
   *
   * @param gamePartyId Id пати.
   * @param clientId Id подключающегося клиента.
   */
  public void processJoinParty(UUID gamePartyId, UUID clientId) {
    GameParty gameParty = gameParties.get(gamePartyId);
    if (!gameParty.getPlayers().isFull()) {
      GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
      sendGamePartyInfo(clientId, gamePartyInfoDto);

      gameParty.addPlayer(new HumanPlayer(PlayerType.SECOND_PLAYER, clientId));
      gameParties.put(gameParty.getGamePartyId(), gameParty);
    }
  }

  /**
   * Метод обработки игровых действий со стороны клиентов.
   *
   * @param clientDto Запрос с действием.
   * @throws GameException Если действие некорректно.
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

  /**
   * Метод отправки информации о пати для клиентов.
   *
   * @param clientId Id клиента, которому отправляется инф.
   * @param gamePartyInfoDto Информация о пати.
   */
  public void sendGamePartyInfo(UUID clientId, GamePartyInfoDto gamePartyInfoDto) {
    String gamePartyInfo;
    try {
      gamePartyInfo = JsonConverter.serialize(gamePartyInfoDto);
      ClientManager.getInstance().sendMessage(clientId, gamePartyInfo);

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
