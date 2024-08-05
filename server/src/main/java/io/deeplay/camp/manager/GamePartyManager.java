package io.deeplay.camp.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.GameParty;
import io.deeplay.camp.JsonConverter;
import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.GiveUpDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.OfferGiveUpDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.dto.server.ConnectionErrorCode;
import io.deeplay.camp.dto.server.GamePartyInfoDto;
import io.deeplay.camp.dto.server.OfferGiveUpServerDto;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.exceptions.GameManagerException;
import io.deeplay.camp.mechanics.PlayerType;
import io.deeplay.camp.player.AiPlayer;
import io.deeplay.camp.player.HumanPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamePartyManager implements Runnable {
  private final Map<UUID, GameParty> gameParties;
  private static final Logger logger = LoggerFactory.getLogger(GamePartyManager.class);
  Thread checkEndedThread;

  public GamePartyManager() {
    this.gameParties = new ConcurrentHashMap<>();
    checkEndedThread = new Thread(this);
    checkEndedThread.setDaemon(true);
    checkEndedThread.start();
  }

  /**
   * Метод, распределяющий запросы на создание/подключение к пати.
   *
   * @param clientDto Запрос клиента.
   */
  public void processCreateOrJoinGameParty(ClientDto clientDto) throws GameManagerException {
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
      default -> {
        logger.error("Ошибка создания или присоединения к игре");
        throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
      }
    }
  }

  /**
   * Метод обаботки создания игры.
   *
   * @param clientId Id клиента, сделавшего запрос.
   * @param gameType Тип игры.
   */
  public void processCreateGameParty(UUID clientId, GameType gameType) throws GameManagerException {
    if (gameType == null) {
      throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
    }
    switch (gameType) {
      case HUMAN_VS_BOT -> createHumanVsBotParty(clientId);
      case HUMAN_VS_HUMAN -> createHumanVsHumanParty(clientId);
      default -> {
        logger.error("Ошибка выбора типа игры");
        throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
      }
    }
  }

  /**
   * Метод создания онлайн игры. Создаёт пати и сразу добавляет клиента как игрока. Затем отправляет
   * ему информацию о пати.
   *
   * @param firstHumanPlayerId Id клиента, запросившего создание.
   */
  private void createHumanVsHumanParty(UUID firstHumanPlayerId) throws GameManagerException {
    try {
      GameParty gameParty = new GameParty(UUID.randomUUID());
      gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, firstHumanPlayerId));
      gameParties.put(gameParty.getGamePartyId(), gameParty);

      GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
      sendGamePartyInfo(firstHumanPlayerId, gamePartyInfoDto);
    } catch (GameManagerException e) {
      logger.error("Ошибка в создании игры между игроком и игроком{}", e.getConnectionErrorCode());
      throw e;
    } catch (Exception e) {
      logger.error("Не известная ошибка в создании игры между игроком и игроком{}", e.getMessage());
      throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
    }
  }

  /**
   * Метод создания игры с ботом. Создаёт пати и сразу добавляет клиента и бота как игроков. Затем
   * отправляется инф. о пати клиенту.
   *
   * @param humanPlayerId Id клиента, запросившего создание.
   */
  private void createHumanVsBotParty(UUID humanPlayerId) throws GameManagerException {
    try {
      GameParty gameParty = new GameParty(UUID.randomUUID());
      gameParty.addPlayer(new HumanPlayer(PlayerType.FIRST_PLAYER, humanPlayerId));
      gameParties.put(gameParty.getGamePartyId(), gameParty);

      GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
      sendGamePartyInfo(humanPlayerId, gamePartyInfoDto);

      gameParty.addPlayer(new AiPlayer(PlayerType.SECOND_PLAYER, gameParty));
    } catch (GameManagerException e) {
      logger.error(
          "Ошибка при создании игры между игроком и ботом {} ", e.getConnectionErrorCode());
      throw e;
    } catch (Exception e) {
      logger.error("Неизвестная ошибка при создании игры между игроком и ботом {}", e.getMessage());
      throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
    }
  }

  /**
   * Метод обработки подключения к пати. Добавляет игрока в пати, если она не полная.
   *
   * @param gamePartyId Id пати.
   * @param clientId Id подключающегося клиента.
   */
  public void processJoinParty(UUID gamePartyId, UUID clientId) throws GameManagerException {
    try {
      if (!gameParties.containsKey(gamePartyId)) {
        logger.error("Ошибка соединения. Не существующая гей пати");
        throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
      }
      GameParty gameParty = gameParties.get(gamePartyId);
      if (!gameParty.getPlayers().isFull()) {
        GamePartyInfoDto gamePartyInfoDto = new GamePartyInfoDto(gameParty.getGamePartyId());
        sendGamePartyInfo(clientId, gamePartyInfoDto);
        gameParty.addPlayer(new HumanPlayer(PlayerType.SECOND_PLAYER, clientId));
        gameParties.put(gameParty.getGamePartyId(), gameParty);
      } else {
        logger.error("Ошибка подключения.При присоединении к игровой сеccии");
        throw new GameManagerException(ConnectionErrorCode.FULL_PARTY);
      }
    } catch (GameManagerException e) {
      throw e;
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
    }
  }

  public void popUpWindow(UUID gamePartyId, UUID clientId) throws GameManagerException {
    String message;
    try{
    GameParty gameParty = gameParties.get(gamePartyId);
      OfferGiveUpServerDto offerGiveUpServerDto =
          new OfferGiveUpServerDto(gameParty.getGamePartyId());
    if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
      message = JsonConverter.serialize(offerGiveUpServerDto);
      logger.info("Предложение сдаться для 2 игрока");
      ClientManager.getInstance().sendMessage(gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
    }
    else {
      message = JsonConverter.serialize(offerGiveUpServerDto);
      logger.error("Предложение сдаться для 1 игрока");
      ClientManager.getInstance().sendMessage(gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
    }
    }catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }
  /**
   * Метод обработки игровых действий со стороны клиентов.
   *
   * @param clientDto Запрос с действием.
   * @throws GameException Если действие некорректно.
   */
  public void processGameAction(ClientDto clientDto) throws GameException, GameManagerException {
    switch (clientDto.getClientDtoType()) {
      case MAKE_MOVE -> {
        MakeMoveDto makeMoveDto = (MakeMoveDto) clientDto;
        GameParty gameParty = gameParties.get(makeMoveDto.getGamePartyId());
        if (gameParty == null){
          logger.error("Ошибка gameparty null. MakeMove");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processMakeMove(makeMoveDto);
      }
      case PLACE_UNIT -> {
        PlaceUnitDto placeUnitDto = (PlaceUnitDto) clientDto;
        GameParty gameParty = gameParties.get(placeUnitDto.getGamePartyId());
        if (gameParty == null){
          logger.error("Ошибка gameparty null. PlaceUnit");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processPlaceUnit(placeUnitDto);
      }
      case CHANGE_PLAYER -> {
        ChangePlayerDto changePlayerDto = (ChangePlayerDto) clientDto;
        GameParty gameParty = gameParties.get(changePlayerDto.getGamePartyId());
        if (gameParty == null){
          logger.error("Ошибка gameparty null. Change player");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processChangePlayer(changePlayerDto);
      }
      case GIVE_UP -> {
        GiveUpDto giveUpDto = (GiveUpDto) clientDto;
        GameParty gameParty = gameParties.get(giveUpDto.getGamePartyId());
        if (gameParty == null){
          logger.error("Ошибка gameparty null. Giveup");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processGiveUp(giveUpDto);
      }
      case OFFER_GIVE_UP -> {
        OfferGiveUpDto offerGiveUpDto = (OfferGiveUpDto) clientDto;
        UUID clientId = offerGiveUpDto.getClientId();
        UUID gamePartyId = offerGiveUpDto.getGamePartyId();
        popUpWindow(gamePartyId, clientId);
      }
      default -> {
        logger.error("Не возможное действие");
        throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
      }
    }
  }

  /**
   * Метод отправки информации о пати для клиентов.
   *
   * @param clientId Id клиента, которому отправляется инф.
   * @param gamePartyInfoDto Информация о пати.
   */
  public void sendGamePartyInfo(UUID clientId, GamePartyInfoDto gamePartyInfoDto)
      throws GameManagerException {
    String gamePartyInfo;
    try {
      gamePartyInfo = JsonConverter.serialize(gamePartyInfoDto);
      ClientManager.getInstance().sendMessage(clientId, gamePartyInfo);

    } catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      for (GameParty games : gameParties.values()) {
        if (games.isGameEnded()) {
          gameParties.remove(games.getGamePartyId());
        }
      }
    }
  }
}
