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
import io.deeplay.camp.server.manager.ClientManager;
import io.deeplay.camp.server.manager.GamePartyManager;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс сессии. Отвечает за диспетчеризацию запросов клиента. */
public class Session implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(Session.class);
  private final ClientHandler clientHandler;
  private final GamePartyManager gamePartyManager;

  public Session(Socket clientSocket, GamePartyManager gamePartyManager) {
    this.clientHandler = new ClientHandler(clientSocket);
    ClientManager.getInstance().addClient(clientHandler.getClientId(), clientHandler);
    this.gamePartyManager = gamePartyManager;
  }

  @Override
  public void run() {
    try {
      String requestJson;
      while ((requestJson = clientHandler.readRequest()) != null) {
        ClientDto clientDto = JsonConverter.deserialize(requestJson, ClientDto.class);
        // Чтобы из клиента каждый раз не тащить его id - будем присваивать его при получении.
        // Зато теперь id клиента хранится только на сервере
        clientDto.setClientId(clientHandler.getClientId());
        handleRequest(clientDto);
      }
    } catch (JsonProcessingException e) {
      logger.error("Json Session error", e);
    } catch (Exception e) {
      logger.error("Session error", e);
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
        default:
          throw new GameManagerException(ConnectionErrorCode.UNIDENTIFIED_ERROR);
      }
    } catch (Exception e) {
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
    } else if (e instanceof GameException gameException) {
      logger.error("{} {}", gameException.getErrorCode(), gameException.getMessage());
      sendErrorToClient(
          new ErrorGameResponseDto(gameException.getErrorCode(), gameException.getMessage()));
    } else {
      logger.error("{}", e.getMessage());
    }
  }

  /** Метод закрывает все ресурсы сессии. */
  public void closeResources() {
    clientHandler.closeResources();
  }
}
