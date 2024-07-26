package io.deeplay.camp;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.manager.GamePartyManager;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс сессии. Отвечает за диспетчерезацию запросов клиента.
 */
public class Session implements Runnable {
    private final ClientHandler clientHandler;
    private final GamePartyManager gamePartyManager;
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    public Session(Socket clientSocket, GamePartyManager gamePartyManager) {
        this.clientHandler = new ClientHandler(clientSocket);
        this.gamePartyManager = gamePartyManager;
    }

    @Override
    public void run() {
        try {
            String requestJson;
            while ((requestJson = clientHandler.readRequest()) != null) {
                Request request = JsonConverter.deserialize(requestJson, Request.class);
                //чтобы из клиента каждый раз не тащить его id - будем присваивать его при получении.
                //Зато теперь id клиента хранится только в сессии
                request.setClientId(clientHandler.getClientId());
                handleRequest(request);
            }
        } catch (Exception e) {
            logger.error("Session error", e);
        } finally {
            closeResources();
        }
    }

    /**
     * Метод получает обрабатывает запрос клиента и направляет его в нужное место.
     * @param request Запрос.
     */
    private void handleRequest(Request request) {
        switch (request.getRequestType()) {
            case CREATE_PARTY:
                gamePartyManager.createParty(request);
                return;
            case JOIN_PARTY:
                gamePartyManager.joinParty(request);
                return;
            case MAKE_MOVE,PLACE_UNIT,CHANGE_PLAYER:
                gamePartyManager.delegateAction(request);
                return;
            case DISCONNECT:
                closeResources();
                closeParties();
                return;
            default:
        }
    }

    /**
     * Метод закрывает все ресурсы сессии.
     */
    public void closeResources() {
        clientHandler.closeResources();
    }

    /**
     * Метод заканчивает все партии в которых участвует клиент.
     */
    public void closeParties() {
        // Logic to close parties
    }
}
