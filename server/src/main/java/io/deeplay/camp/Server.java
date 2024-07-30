package io.deeplay.camp;

import io.deeplay.camp.manager.GamePartyManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс сервера. Отвечает за прием клиентских подключений и создание сессий. */
public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private final GamePartyManager gamePartyManager;
  private final int port;
  private ServerSocket serverSocket;

  public Server(int port) {
    this.port = port;
    this.gamePartyManager = new GamePartyManager();
  }

  /** Запуск сервера. */
  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Server started on port {}", port);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        logger.info("New client connected: {}", clientSocket.getInetAddress());

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
}
