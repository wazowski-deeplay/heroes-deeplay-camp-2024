package io.deeplay.camp;

import com.sun.jdi.request.ExceptionRequest;
import io.deeplay.camp.dto.server.Response;

import java.util.HashMap;
import java.util.UUID;

public class SessionManager {
    private HashMap<UUID, Session> sessions;
    public SessionManager() {
         this.sessions = new HashMap<>();
    }
    public void sendResponse(UUID uuid,Response response) {
        String responseJson = "";
        try {
            responseJson = JsonConverter.serialize(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessions.get(uuid).sendResponse(responseJson);
    }

    public void add(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public void removeSession(UUID sessionId) {
        sessions.remove(sessionId);
    }
}

