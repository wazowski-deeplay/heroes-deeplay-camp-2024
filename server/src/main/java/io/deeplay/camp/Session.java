package io.deeplay.camp;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.manager.ClientManager;
import io.deeplay.camp.manager.GamePartyManager;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс сессии. Отвечает за диспетчерезацию запросов клиента. */
public class Session implements Runnable {
  private final ClientHandler clientHandler;
  private final GamePartyManager gamePartyManager;
  private static final Logger logger = LoggerFactory.getLogger(Session.class);

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
        // чтобы из клиента каждый раз не тащить его id - будем присваивать его при получении.
        // Зато теперь id клиента хранится только на сервере
        clientDto.setClientId(clientHandler.getClientId());
        handleRequest(clientDto);
      }
    } catch (Exception e) {
      logger.error("Session error", e);
    } finally {
      logger.info("Session closed");
      closeResources();
    }
  }

  /**
   * Метод получает обрабатывает запрос клиента и направляет его в нужное место.
   *
   * @param clientDto Запрос.
   */
  private void handleRequest(ClientDto clientDto) {
    try {
      switch (clientDto.getClientDtoType()) {
        case CREATE_PARTY, JOIN_PARTY:
          gamePartyManager.processCreateOrJoinGameParty(clientDto);
          return;
        case MAKE_MOVE, PLACE_UNIT, CHANGE_PLAYER:
          gamePartyManager.processGameAction(clientDto);
          return;
        case DISCONNECT:
          closeResources();
          return;
        default:
      }
    } catch (GameException e) {
      // формируем ответ об ошибке и отправляем
    }
  }

  /** Метод закрывает все ресурсы сессии. */
  public void closeResources() {
    clientHandler.closeResources();
  }
}
