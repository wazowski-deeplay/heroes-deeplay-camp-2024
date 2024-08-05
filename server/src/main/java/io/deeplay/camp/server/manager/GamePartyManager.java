package io.deeplay.camp.server.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.DrawDto;
import io.deeplay.camp.core.dto.client.game.GiveUpDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.OfferDrawDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.DrawServerDto;
import io.deeplay.camp.core.dto.server.GamePartiesDto;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.OfferDrawServerDto;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.PlayerType;
import io.deeplay.camp.server.GameParty;
import io.deeplay.camp.server.exceptions.GameManagerException;
import io.deeplay.camp.server.player.AiPlayer;
import io.deeplay.camp.server.player.HumanPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamePartyManager {
  private static final Logger logger = LoggerFactory.getLogger(GamePartyManager.class);
  private final Map<UUID, GameParty> gameParties;
  private final ScheduledExecutorService executorService;

  public GamePartyManager() {
    this.gameParties = new ConcurrentHashMap<>();
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    scheduleCheckEndedTask();
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
      logger.info("Партия создалась");
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
      logger.info("");
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

  public void offerDraw(UUID gamePartyId, UUID clientId) throws GameManagerException {
    String message;
    try {
      GameParty gameParty = gameParties.get(gamePartyId);
      OfferDrawServerDto offerGiveUpServerDto =
          new OfferDrawServerDto(gameParty.getGamePartyId());
      if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
        message = JsonConverter.serialize(offerGiveUpServerDto);
        logger.info("Предложение ничьи для 2 игрока");
        gameParty.setDraw(0,true);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
      } else {
        message = JsonConverter.serialize(offerGiveUpServerDto);
        logger.error("Предложение ничьи для 1 игрока");
        gameParty.setDraw(1,true);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
      }
    } catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }

  public void acceptDraw(UUID gamePartyId, UUID clientId) throws GameManagerException {
    String message;
    try{
    GameParty gameParty = gameParties.get(gamePartyId);
    DrawServerDto drawServerDto = new DrawServerDto(gameParty.getGamePartyId());
    if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
      message = JsonConverter.serialize(drawServerDto);
      logger.info("Подтверждение ничьи первым игроком");
      gameParty.setDraw(0,true);
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
        if (gameParty == null) {
          logger.error("Ошибка gameparty null. MakeMove");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processMakeMove(makeMoveDto);
      }
      case PLACE_UNIT -> {
        PlaceUnitDto placeUnitDto = (PlaceUnitDto) clientDto;
        GameParty gameParty = gameParties.get(placeUnitDto.getGamePartyId());
        if (gameParty == null) {
          logger.error("Ошибка gameparty null. PlaceUnit");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processPlaceUnit(placeUnitDto);
      }
      case CHANGE_PLAYER -> {
        ChangePlayerDto changePlayerDto = (ChangePlayerDto) clientDto;
        GameParty gameParty = gameParties.get(changePlayerDto.getGamePartyId());
        if (gameParty == null) {
          logger.error("Ошибка gameparty null. Change player");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processChangePlayer(changePlayerDto);
      }
      case GIVE_UP -> {
        GiveUpDto giveUpDto = (GiveUpDto) clientDto;
        GameParty gameParty = gameParties.get(giveUpDto.getGamePartyId());
        if (gameParty == null) {
          logger.error("Ошибка gameparty null. Giveup");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        gameParty.processGiveUp(giveUpDto);
      }
      case OFFER_DRAW -> {
        OfferDrawDto offerDrawDto = (OfferDrawDto) clientDto;
        if(gameParties.get(offerDrawDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. OfferDraw");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = offerDrawDto.getClientId();
        UUID gamePartyId = offerDrawDto.getGamePartyId();
        offerDraw(gamePartyId, clientId);
      }
      case DRAW -> {
        DrawDto drawDto = (DrawDto) clientDto;
        if(gameParties.get(drawDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. Draw");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = drawDto.getClientId();
        UUID gamePartyId = drawDto.getGamePartyId();

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

  public void processGetParties(ClientDto clientDto) throws JsonProcessingException {
    List<UUID> partiesIds = new ArrayList<>(gameParties.keySet());
    GamePartiesDto gamePartiesDto = new GamePartiesDto(partiesIds);
    UUID clientId = clientDto.getClientId();
    ClientManager.getInstance().sendMessage(clientId, JsonConverter.serialize(gamePartiesDto));
  }

  private void scheduleCheckEndedTask() {
    executorService.scheduleAtFixedRate(
        () -> {
          for (GameParty gameParty : gameParties.values()) {
            if (gameParty.isGameEnded()) {
              gameParties.remove(gameParty.getGamePartyId());
            }
          }
        },
        5,
        5,
        TimeUnit.SECONDS);
  }

  public void shutdown() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
    }
  }
}
