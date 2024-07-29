package io.deeplay.camp.manager;

import io.deeplay.camp.ClientHandler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс-синглтон, хранящий в себе всех клиентов. */
public class ClientManager {
  private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  private final Map<UUID, ClientHandler> clients;

  private static ClientManager instance;

  private ClientManager() {
    this.clients = new ConcurrentHashMap<>();
  }

  public static synchronized ClientManager getInstance() {
    if (instance == null) {
      instance = new ClientManager();
    }
    return instance;
  }

  public void addClient(UUID id, ClientHandler handler) {
    clients.put(id, handler);
  }

  public void removeClient(UUID id) {
    clients.remove(id);
  }

  /**
   * Метод отправки сообщений клиенту по его ID.
   *
   * @param clientId id клиента.
   * @param message Отправляемое сообщение.
   */
  public void sendMessage(UUID clientId, String message) {
    try {
      clients.get(clientId).sendMessage(message);
    } catch (Exception e) {
      logger.info("Невозможно отправить сообщение клиенту: {}", clientId);
    }
  }
}
