package io.deeplay.camp;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.server.Response;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionProcessor implements Runnable {
    private final Socket socket;
    private final GamePartyManager gamePartyManager;
    private final SessionManager sessionManager;
    private static final Logger logger = LoggerFactory.getLogger(SessionProcessor.class);

    public SessionProcessor(Socket socket, GamePartyManager gamePartyManager, SessionManager sessionManager) {
        this.socket = socket;
        this.gamePartyManager = gamePartyManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        try (Session session = new Session(socket)) {
            sessionManager.add(session);
            while (!session.isClosed()) {
                String requestJson = session.waitForRequest();
                if (requestJson == null) {
                   break;
                }
                Request request = JsonConverter.deserialize(requestJson, Request.class);
                Response response = handleRequest(request);
                String responseJson = JsonConverter.serialize(response);
                session.sendResponse(responseJson);
            }
            sessionManager.removeSession(session.getSessionId());
        } catch (IOException e) {
            logger.error("Connection error", e);
        }
    }

    private Response handleRequest(Request request) {
        switch (request.getRequestType()) {
            case CREATE_PARTY:
                return gamePartyManager.createParty(request);
            case JOIN_PARTY:
                return gamePartyManager.joinParty(request);
            case MAKE_MOVE:
            case PLACE_UNIT:
            case CHANGE_PLAYER:
                return gamePartyManager.delegateAction(request);
            case DISCONNECT:
            default:
        }
        return null;
    }
}
