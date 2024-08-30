package io.deeplay.camp.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.ErrorConnectionResponseDto;
import io.deeplay.camp.core.dto.server.ErrorGameResponseDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.server.exceptions.GameManagerException;
import io.deeplay.camp.server.exceptions.GamePartyException;
import io.deeplay.camp.server.manager.ClientManager;
import io.deeplay.camp.server.manager.GamePartyManager;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс сессии. Отвечает за диспетчеризацию запросов клиента. */
public class Session implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(Session.class);
  private final ClientHandler clientHandler;
  private final GamePartyManager gamePartyManager;
  private final InfluxDBService influxDBService;
  private final AtomicInteger requestCount;
  private final AtomicInteger errorCount;
  private int sessionCount;


  public Session(Socket clientSocket, GamePartyManager gamePartyManager, AtomicInteger requestCount, AtomicInteger errorCount, int sessionCount) {
    this.clientHandler = new ClientHandler(clientSocket);
    ClientManager.getInstance().addClient(clientHandler.getClientId(), clientHandler);
    this.gamePartyManager = gamePartyManager;
    this.influxDBService = new InfluxDBService();
    this.requestCount = requestCount;
    this.errorCount = errorCount;
    this.sessionCount = sessionCount;

  }

  @Override
  public void run() {
    try {
      String requestJson;
      while ((requestJson = clientHandler.readRequest()) != null) {
        requestCount.getAndIncrement();
        long startTime = System.currentTimeMillis();
        ClientDto clientDto = JsonConverter.deserialize(requestJson, ClientDto.class);
        clientDto.setClientId(clientHandler.getClientId());
        handleRequest(clientDto);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        influxDBService.writeData("response_time","e_time",(double) elapsedTime);
      }
    } catch (JsonProcessingException e) {
      logger.error("Json Session error", e);
      errorCount.incrementAndGet();
    } catch (Exception e) {
      logger.error("Session error", e);
      errorCount.incrementAndGet();
    } finally {
      logger.info("Session closed");
      closeResources();
    }
  }

  private void sendErrorToClient(ServerDto errorConnectionResponseDto) {
    try {
      String errorResponse = JsonConverter.serialize(errorConnectionResponseDto);
      clientHandler.sendMessage(errorResponse);
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize error response", e);
    }
  }

  /**
   * Метод обрабатывает запрос клиента и направляет его в нужное место.
   *
   * @param clientDto Запрос.
   */
  private void handleRequest(ClientDto clientDto) {
    try {
      switch (clientDto.getClientDtoType()) {
        case CREATE_PARTY, JOIN_PARTY:
          gamePartyManager.processCreateOrJoinGameParty(clientDto);
          return;
        case MAKE_MOVE, PLACE_UNIT, CHANGE_PLAYER, GIVE_UP, OFFER_DRAW, DRAW, SWITCH_PARTY, OFFER_RESTART_GAME, RESTART:
          gamePartyManager.processGameAction(clientDto);
          return;
        case GET_PARTIES:
          gamePartyManager.processGetParties(clientDto);
          return;
        case DISCONNECT:
          closeResources();
          return;
        case EXIT_PARTY:
          gamePartyManager.processExitGame(clientDto);
          return;
        default:
          throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
      }
    } catch (Exception e) {
      errorCount.incrementAndGet();
      handleException(e);
    }
  }

  private void handleException(Exception e) {
    if (e instanceof GameManagerException gameManagerException) {
      logger.error(
          "{} {}",
          gameManagerException.getConnectionErrorCode(),
          gameManagerException.getMessage());
      sendErrorToClient(
          new ErrorConnectionResponseDto(
              gameManagerException.getConnectionErrorCode(), gameManagerException.getMessage()));
    } else if (e instanceof GamePartyException gamePartyException) {
      logger.error("{} {}", gamePartyException.getGameException().getErrorCode(), gamePartyException.getGameException().getMessage());
      sendErrorToClient(
          new ErrorGameResponseDto(gamePartyException.getGameException().getErrorCode(),
                  gamePartyException.getGameException().getMessage(), gamePartyException.getGamePartyId()));
    } else {
      logger.error("{}", e.getMessage());
    }
  }

  /** Метод закрывает все ресурсы сессии. */
  public void closeResources() {
    sessionCount--;
    influxDBService.writeData("game_sesion","count_session",sessionCount);
    influxDBService.closeInfluxConnection();
    clientHandler.closeResources();
  }
}
