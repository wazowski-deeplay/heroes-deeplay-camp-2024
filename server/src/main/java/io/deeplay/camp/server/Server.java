package io.deeplay.camp.server;

import io.deeplay.camp.server.manager.GamePartyManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс сервера. Отвечает за прием клиентских подключений и создание сессий. */
public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private final GamePartyManager gamePartyManager;
  private final int port;
  private ServerSocket serverSocket;
  private final InfluxDBService influxDBService;
  private final AtomicInteger requestCount = new AtomicInteger(0);
  private final AtomicInteger errorCount = new AtomicInteger(0);
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private int sessionCount;
  public Server(int port) {
    this.port = port;
    this.gamePartyManager = new GamePartyManager();
    this.influxDBService = new InfluxDBService();
  }

  /** Запуск сервера. */
  public void start() {
    scheduler.scheduleAtFixedRate(this::logMetrics, 0, 40, TimeUnit.SECONDS);

    try {
      serverSocket = new ServerSocket(port);
      logger.info("Server started on port {}", port);

      while (true) {
        sessionCount++;

        Socket clientSocket = serverSocket.accept();
        logger.info("New client connected: {}", clientSocket.getInetAddress());

        Session session = new Session(clientSocket, gamePartyManager, requestCount,errorCount,sessionCount);
        new Thread(session).start();
        influxDBService.writeData("game_sesion","count_session",sessionCount);
      }
    } catch (IOException e) {
      logger.error("Server error", e);
    } finally {
      stop();
    }
  }

  private void logMetrics() {
    int currentRequestCount = requestCount.getAndSet(0); // Получаем текущее значение и сбрасываем счетчик
    int currentErrorCount = errorCount.getAndSet(0); // Получаем текущее значение и сбрасываем счетчик ошибок

    // Отправляем метрики в InfluxDB
    influxDBService.writeData("throughput", "requests_per_minute", currentRequestCount);
    influxDBService.writeData("error_rate", "errors_per_minute", currentErrorCount);
    influxDBService.writeData("error_ratio", "error_ratio", currentRequestCount == 0 ? 0 : (double) currentErrorCount / currentRequestCount);

    logger.info("Throughput in the last minute: {} requests", currentRequestCount);
    logger.info("Errors in the last minute: {} errors", currentErrorCount);
    logger.info("Error ratio in the last minute: {}%", currentRequestCount == 0 ? 0 : (double) currentErrorCount / currentRequestCount * 100);
  }

  /** Остановка сервера. */
  public void stop() {
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
      scheduler.shutdownNow();
    } catch (IOException e) {
      logger.error("Error closing server socket", e);
    }
  }
}
