package io.deeplay.camp;

import io.deeplay.camp.manager.GamePartyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** Класс сервера. Отвечает за прием клиентских подключений и создание сессий. */
public class Server {
  private static final int PORT = 12345;
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private final GamePartyManager gamePartyManager;
  private ServerSocket serverSocket;

  public Server() {
    this.gamePartyManager = new GamePartyManager();
  }

  /** Запуск сервера. */
  public void start() {
    try {
      serverSocket = new ServerSocket(PORT);
      logger.info("Server started on port " + PORT);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        logger.info("New client connected: " + clientSocket.getInetAddress());

        Session session = new Session(clientSocket, gamePartyManager);
        new Thread(session).start();
      }
    } catch (IOException e) {
      logger.error("Server error", e);
    } finally {
      stop();
    }
  }

  /** Остановка сервера. */
  public void stop() {
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
    } catch (IOException e) {
      logger.error("Error closing server socket", e);
    }
  }

  public static void main(String[] args) {
    Server server = new Server();
    server.start();
  }
}
