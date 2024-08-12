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
import io.deeplay.camp.core.dto.client.game.SwitchPartyDto;
import io.deeplay.camp.core.dto.client.game.*;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.core.dto.client.party.ExitGamePartyDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.core.dto.server.*;
import io.deeplay.camp.game.events.DrawEvent;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.GamePartiesDto;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.OfferDrawServerDto;
import io.deeplay.camp.game.events.DrawEvent;
import io.deeplay.camp.core.dto.server.*;
import io.deeplay.camp.core.dto.server.OfferRestartServerDto;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.PlayerType;
import io.deeplay.camp.server.GameParty;
import io.deeplay.camp.server.exceptions.GameManagerException;
import io.deeplay.camp.server.exceptions.GamePartyException;
import io.deeplay.camp.server.player.AiPlayer;
import io.deeplay.camp.server.player.HumanPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    // scheduleCheckEndedTask();
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
      case BOT_VS_BOT -> createBotVsBotParty(clientId);
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
      gameParties.put(gameParty.getGamePartyId(), gameParty);
      gameParty.addPlayer(
          new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), firstHumanPlayerId));
      logger.info("Партия создалась");
      GamePartyInfoDto gamePartyInfoDto =
          new GamePartyInfoDto(
              gameParty.getGamePartyId(),
              gameParty.getPlayers().getPlayerTypeById(firstHumanPlayerId));
      sendGamePartyInfo(firstHumanPlayerId, gamePartyInfoDto);
    } catch (GameManagerException e) {
      logger.error("Ошибка в создании игры между игроком и игроком{}", e.getConnectionErrorCode());
      throw e;
    } catch (Exception e) {
      logger.error("Не известная ошибка в создании игры между игроком и игроком{}", e.getMessage());
      throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
    }
  }

  private PlayerType randomPlayerType(UUID gamePartyId) throws GameManagerException {
    List<PlayerType> randomPayerTypesValues = new ArrayList<>();
    randomPayerTypesValues.add(PlayerType.FIRST_PLAYER);
    randomPayerTypesValues.add(PlayerType.SECOND_PLAYER);
    if (gameParties.get(gamePartyId).getPlayers().getHashMap().isEmpty()) {
      Random rand = new Random();
      return randomPayerTypesValues.get(rand.nextInt(randomPayerTypesValues.size()));
    } else if (gameParties
        .get(gamePartyId)
        .getPlayers()
        .getHashMap()
        .containsKey(PlayerType.SECOND_PLAYER)) {
      return PlayerType.FIRST_PLAYER;
    } else if (gameParties
        .get(gamePartyId)
        .getPlayers()
        .getHashMap()
        .containsKey(PlayerType.FIRST_PLAYER)) {
      return PlayerType.SECOND_PLAYER;
    } else {
      return null;
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
      gameParties.put(gameParty.getGamePartyId(), gameParty);
      gameParty.addPlayer(
          new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), humanPlayerId));

      GamePartyInfoDto gamePartyInfoDto =
          new GamePartyInfoDto(
              gameParty.getGamePartyId(), gameParty.getPlayers().getPlayerTypeById(humanPlayerId));
      sendGamePartyInfo(humanPlayerId, gamePartyInfoDto);
      logger.info("");
      gameParty.addPlayer(new AiPlayer(randomPlayerType(gameParty.getGamePartyId()), gameParty));
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
   * Метод создания битвы бота с ботом. Создаёт пати и сразу добавляет бота и бота как игроков.
   *
   *
   * @param humanPlayerId Id клиента, запросившего создание.
   */
  private void createBotVsBotParty(UUID humanPlayerId) throws GameManagerException {
    try {
      GameParty gameParty = new GameParty(UUID.randomUUID());
      gameParties.put(gameParty.getGamePartyId(), gameParty);
      gameParty.addPlayer(new AiPlayer(randomPlayerType(gameParty.getGamePartyId()), gameParty));
      gameParty.addPlayer(new AiPlayer(randomPlayerType(gameParty.getGamePartyId()), gameParty));
      logger.info("");
    } catch (GameManagerException e) {
      logger.error(
              "Ошибка при создании игры между ботом и ботом {} ", e.getConnectionErrorCode());
      throw e;
    } catch (Exception e) {
      logger.error("Неизвестная ошибка при создании игры между ботом и ботом {}", e.getMessage());
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
        GamePartyInfoDto gamePartyInfoDto =
            new GamePartyInfoDto(
                gameParty.getGamePartyId(), randomPlayerType(gameParty.getGamePartyId()));
        sendGamePartyInfo(clientId, gamePartyInfoDto);

        gameParty.addPlayer(
            new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), clientId));
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

  public void offerDraw(UUID gamePartyId, UUID clientId) throws GameManagerException, GamePartyException {
    String message;
    try {
      GameParty gameParty = gameParties.get(gamePartyId);
      OfferDrawServerDto offerGiveUpServerDto = new OfferDrawServerDto(gameParty.getGamePartyId());
      if (gameParty.getPlayers().getHashMap().get(PlayerType.SECOND_PLAYER) instanceof AiPlayer) {
        DrawEvent drawEvent =
            ((AiPlayer) gameParty.getPlayers().getHashMap().get(PlayerType.SECOND_PLAYER))
                .getBot()
                .generateDrawEvent(gameParty.getGame().getGameState());
        logger.info(" Результаты передаваемые AI player {}", drawEvent.getDraw());
        gameParty.processDraw(drawEvent.getDraw());
      } else {
        if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
          message = JsonConverter.serialize(offerGiveUpServerDto);
          logger.info("Предложение ничьи для 2 игрока");
          gameParty.setIndexDraw(0, true);
          ClientManager.getInstance()
              .sendMessage(
                  gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
        } else {
          message = JsonConverter.serialize(offerGiveUpServerDto);
          logger.error("Предложение ничьи для 1 игрока");
          gameParty.setIndexDraw(1, true);
          ClientManager.getInstance()
              .sendMessage(
                  gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
        }
      }
    } catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }

  public void acceptDraw(UUID gamePartyId, UUID clientId) throws GamePartyException {
      GameParty gameParty = gameParties.get(gamePartyId);
      if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
        logger.info("Подтверждение ничьи первым игроком");
        gameParty.setIndexDraw(0, true);
        gameParty.processDraw(gameParty.getDraw());
      } else if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.SECOND_PLAYER) {
        logger.info("Подтверждение ничьи вторым игроком");
        gameParty.setIndexDraw(1, true);
        gameParty.processDraw(gameParty.getDraw());
      }
  }

  public void offerRestart(UUID gamePartyId, UUID clientId) throws GameManagerException {
    String message;
    try {
      GameParty gameParty = gameParties.get(gamePartyId);
      OfferRestartServerDto offerRestartServerDto =
          new OfferRestartServerDto(gameParty.getGamePartyId());
      if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
        message = JsonConverter.serialize(offerRestartServerDto);
        logger.info("Предложение рестарта для 2 игрока");
        gameParty.setRestart(0, true);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
      } else {
        message = JsonConverter.serialize(offerRestartServerDto);
        logger.error("Предложение рестарта для 1 игрока");
        gameParty.setRestart(1, true);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
      }
    } catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }

  public void acceptRestart(UUID gamePartyId, UUID clientId) throws GameManagerException, GamePartyException {
      GameParty gameParty = gameParties.get(gamePartyId);
      if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
        logger.info("Подтверждение рестарта первым игроком");
        gameParties.get(gamePartyId).setRestart(0, true);
        if (gameParty.getPlayers().getHashMap().get(PlayerType.SECOND_PLAYER) instanceof AiPlayer) {
          gameParties.get(gamePartyId).setRestart(1, true);
          gameParty.getPlayers().clearPlayersType();

          gameParty.addPlayer(
                  new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), clientId));
          gameParty.addPlayer(new AiPlayer(randomPlayerType(gameParty.getGamePartyId()), gameParty));

          gameParty.processRestart(gameParty.getRestart());

          GamePartyInfoDto gamePartyInfoHumanPlayerDto =
                  new GamePartyInfoDto(
                          gameParty.getGamePartyId(), gameParty.getPlayers().getPlayerTypeById(clientId));
          sendGamePartyInfo(clientId, gamePartyInfoHumanPlayerDto);

        } else {
          UUID anotherPlayerId = gameParty.getPlayers().getPlayerByAnotherPlayerId(clientId);
          gameParty.getPlayers().clearPlayersType();

          gameParty.addPlayer(
              new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), clientId));
          gameParty.addPlayer(
              new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), anotherPlayerId));

          gameParty.processRestart(gameParty.getRestart());

          gameParty.updateGameStateForPlayers();

          GamePartyInfoDto gamePartyInfoFirstPlayerDto =
              new GamePartyInfoDto(
                  gameParty.getGamePartyId(), gameParty.getPlayers().getPlayerTypeById(clientId));
          sendGamePartyInfo(clientId, gamePartyInfoFirstPlayerDto);
          GamePartyInfoDto gamePartyInfoSecondPlayerDto =
              new GamePartyInfoDto(
                  gameParty.getGamePartyId(),
                  gameParty.getPlayers().getPlayerTypeById(anotherPlayerId));
          sendGamePartyInfo(anotherPlayerId, gamePartyInfoSecondPlayerDto);
        }
      } else if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.SECOND_PLAYER) {
        logger.info("Подтверждение рестарта вторым игроком");
        gameParties.get(gamePartyId).setRestart(1, true);
        if (gameParty.getPlayers().getHashMap().get(PlayerType.FIRST_PLAYER) instanceof AiPlayer) {
          gameParties.get(gamePartyId).setRestart(0, true);
          gameParty.getPlayers().clearPlayersType();

          gameParty.addPlayer(
                  new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), clientId));
          gameParty.addPlayer(new AiPlayer(randomPlayerType(gameParty.getGamePartyId()), gameParty));

          gameParty.processRestart(gameParty.getRestart());

          GamePartyInfoDto gamePartyInfoHumanPlayerDto =
                  new GamePartyInfoDto(
                          gameParty.getGamePartyId(), gameParty.getPlayers().getPlayerTypeById(clientId));
          sendGamePartyInfo(clientId, gamePartyInfoHumanPlayerDto);

        } else {
          UUID anotherPlayerId = gameParty.getPlayers().getPlayerByAnotherPlayerId(clientId);

          gameParty.getPlayers().clearPlayersType();

          gameParty.addPlayer(
              new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), clientId));
          gameParty.addPlayer(
              new HumanPlayer(randomPlayerType(gameParty.getGamePartyId()), anotherPlayerId));

          gameParty.processRestart(gameParty.getRestart());

          gameParty.updateGameStateForPlayers();

          GamePartyInfoDto gamePartyInfoFirstPlayerDto =
              new GamePartyInfoDto(
                  gameParty.getGamePartyId(), gameParty.getPlayers().getPlayerTypeById(clientId));
          sendGamePartyInfo(clientId, gamePartyInfoFirstPlayerDto);
          GamePartyInfoDto gamePartyInfoSecondPlayerDto =
              new GamePartyInfoDto(
                  gameParty.getGamePartyId(),
                  gameParty.getPlayers().getPlayerTypeById(anotherPlayerId));
          sendGamePartyInfo(anotherPlayerId, gamePartyInfoSecondPlayerDto);
        }
      }
  }

  public void exitGame(UUID gamePartyId, UUID clientId) throws GameManagerException, GamePartyException{
    String message;
    try {
      GameParty gameParty = gameParties.get(gamePartyId);
      ExitPartyServerDto exitGamePartyDto = new ExitPartyServerDto(gameParty.getGamePartyId());
      if (gameParty.getPlayers().getPlayerTypeById(clientId) == PlayerType.FIRST_PLAYER) {
        message = JsonConverter.serialize(exitGamePartyDto);
        logger.info("Первый игрок покидает игру - " + gamePartyId);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
        gameParties.get(gamePartyId).closeParty(clientId);
        gameParties.remove(gamePartyId);

      } else {
        message = JsonConverter.serialize(exitGamePartyDto);
        logger.error("Второй игрок покидает игру - " + gamePartyId);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.FIRST_PLAYER), message);
        ClientManager.getInstance()
            .sendMessage(
                gameParty.getPlayers().getPlayerByPlayerType(PlayerType.SECOND_PLAYER), message);
        gameParties.get(gamePartyId).closeParty(clientId);
        gameParties.remove(gamePartyId);
      }
    } catch (JsonProcessingException e) {
      throw new GameManagerException(ConnectionErrorCode.SERIALIZABLE_ERROR);
    }
  }

  /**
   * Метод обработки игровых действий со стороны клиентов.
   *
   * @param clientDto Запрос с действием.
   * @throws GameException Если действие некорректно.
   */
  public void processGameAction(ClientDto clientDto) throws GameManagerException, GamePartyException {
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
      case SWITCH_PARTY -> {
        SwitchPartyDto switchPartyDto = (SwitchPartyDto) clientDto;
        GameParty gameParty = gameParties.get(switchPartyDto.getGamePartyId());
        gameParty.processSwitchParty(switchPartyDto);
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
        if (gameParties.get(offerDrawDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. OfferDraw");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = offerDrawDto.getClientId();
        UUID gamePartyId = offerDrawDto.getGamePartyId();
        offerDraw(gamePartyId, clientId);
      }
      case DRAW -> {
        DrawDto drawDto = (DrawDto) clientDto;
        if (gameParties.get(drawDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. Draw");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = drawDto.getClientId();
        UUID gamePartyId = drawDto.getGamePartyId();
        acceptDraw(gamePartyId, clientId);
      }
      case OFFER_RESTART_GAME -> {
        OfferRestartDto offerRestartDto = (OfferRestartDto) clientDto;
        if (gameParties.get(offerRestartDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. OfferRestart");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = offerRestartDto.getClientId();
        UUID gamePartyId = offerRestartDto.getGamePartyId();
        offerRestart(gamePartyId, clientId);
      }
      case RESTART -> {
        RestartDto restartDto = (RestartDto) clientDto;
        if (gameParties.get(restartDto.getGamePartyId()) == null) {
          logger.error("Ошибка gameparty null. Restart");
          throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
        }
        UUID clientId = restartDto.getClientId();
        UUID gamePartyId = restartDto.getGamePartyId();
        acceptRestart(gamePartyId, clientId);
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

  public void processExitGame(ClientDto clientDto) throws GameManagerException, GameException, GamePartyException {
    ExitGamePartyDto exitGamePartyDto = (ExitGamePartyDto) clientDto;
    if (gameParties.get(exitGamePartyDto.getGamePartyId()) == null) {
      logger.error("Ошибка gameparty null. Exit Party");
      throw new GameManagerException(ConnectionErrorCode.NON_EXISTENT_CONNECTION);
    }
    UUID clientId = exitGamePartyDto.getClientId();
    UUID gamePartyId = exitGamePartyDto.getGamePartyId();
    exitGame(gamePartyId, clientId);
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
