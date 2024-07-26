package io.deeplay.camp;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.server.Response;
import java.io.*;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс сессии. Отвечает за диспетчерезацию запросов клиента.
 */
public class Session implements Runnable {
    private Socket clientSocket;
    private GamePartyManager gamePartyManager;
    private BufferedReader reader;
    private BufferedWriter writer;
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    public Session(Socket clientSocket, GamePartyManager gamePartyManager) {
        this.clientSocket = clientSocket;
        this.gamePartyManager = gamePartyManager;
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            logger.error("Error on reader/writer init", e);
        }
    }

    @Override
    public void run() {
        try {
            String requestJson;
            while ((requestJson = reader.readLine()) != null) {
                Request request = JsonConverter.deserialize(requestJson, Request.class);
                Response response = handleRequest(request);
                String responseJson = JsonConverter.serialize(response);
                writer.write(responseJson);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            logger.error("Connection error", e);
        }
    }

    /**
     * Метод получает обрабатывает запрос клиента и в любом случае возвращает ответ.
     * @param request Запрос.
     * @return Ответ.
     */
    private Response handleRequest(Request request) {
        switch (request.getRequestType()) {
            case CREATE_PARTY:
                return gamePartyManager.createParty(request);
            case JOIN_PARTY:
                return gamePartyManager.joinParty(request);
            case MAKE_MOVE,PLACE_UNIT,CHANGE_PLAYER:
                return gamePartyManager.delegateAction(request);
            case DISCONNECT:
                closeResources();
                closeParties();
            default:
                return null;
        }
    }

    /**
     * Метод закрывает все ресурсы сессии.
     */
    public void closeResources() {
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            logger.error("Error closing resources", e);
        }

    }

    /**
     * Метод заканчивает все партии в которых участвует клиент.
     */
    public void closeParties(){

    }
}
