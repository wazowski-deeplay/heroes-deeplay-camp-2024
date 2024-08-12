package io.deeplay.camp.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.app.controller.GameJoinController;
import io.deeplay.camp.app.controller.MainController;
import io.deeplay.camp.app.model.GameModelManager;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.server.ErrorGameResponseDto;
import io.deeplay.camp.core.dto.server.GamePartiesDto;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Platform;
import lombok.Setter;

/**
 * Класс Client представляет собой клиентское приложение, которое взаимодействует с сервером через
 * сокет. Отвечает за отправку и получение сообщений, а также за обработку полученных данных.
 */
public class Client {
  private static volatile Client instance;

  private static String ipAddr = "localhost";
  private static int port = 9090;
  private final Socket socket;
  private final PrintWriter out;
  private final BufferedReader in;
  @Setter private MainController mainController;
  @Setter private GameJoinController gameJoinController;
  private final GameModelManager gameModelManager;

  /**
   * Приватный конструктор для создания экземпляра клиента.
   *
   * @param ipAddr IP-адрес сервера
   * @param port порт сервера
   */
  private Client(String ipAddr, int port) {
    this.ipAddr = ipAddr;
    this.port = port;
    try {
      socket = new Socket(ipAddr, port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      startListeningThread();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.gameModelManager = GameModelManager.getInstance();
  }

  /**
   * Метод для получения единственного экземпляра клиента (Singleton).
   *
   * @return экземпляр клиента
   */
  public static Client getInstance() {
    if (instance == null) {
      synchronized (Client.class) {
        if (instance == null) {
          instance = new Client(ipAddr, port);
        }
      }
    }
    return instance;
  }

  /**
   * Метод для отправки сообщения на сервер.
   *
   * @param request сообщение для отправки
   */
  public void sendMessage(String request) {
    out.println(request);
  }

  /**
   * Метод для обработки входящего сообщения от сервера.
   *
   * @param message входящее сообщение
   * @throws JsonProcessingException если произошла ошибка при десериализации JSON
   */
  public void handleMessage(String message) throws JsonProcessingException {
    ServerDto serverDto = JsonConverter.deserialize(message, ServerDto.class);
    switch (serverDto.getServerDtoType()) {
      case GAME_PARTY_INFO -> {
        GamePartyInfoDto gamePartyInfoDto = (GamePartyInfoDto) serverDto;
        Platform.runLater(() -> mainController.openGame(gamePartyInfoDto));
      }
      case GAME_STATE -> {
        GameStateDto gameStateDto = JsonConverter.deserialize(message, GameStateDto.class);
        gameModelManager.updateGame(gameStateDto);
      }
      case GAME_PARTIES -> {
        GamePartiesDto gamePartiesDto = JsonConverter.deserialize(message, GamePartiesDto.class);
        gameJoinController.setParties(gamePartiesDto);
      }
      case ERROR_GAME_INFO -> {
        ErrorGameResponseDto errorGameResponseDto = (ErrorGameResponseDto) serverDto;
        gameModelManager.handleGameError(errorGameResponseDto);
      }
      case ERROR_CONNECTION_INFO -> {
        // Handle ERROR_CONNECTION_INFO
      }
    }
  }

  /** Метод для запуска потока, который слушает входящие сообщения от сервера. */
  private void startListeningThread() {
    Thread listenerThread =
        new Thread(
            () -> {
              try {
                String message;
                while ((message = in.readLine()) != null) {
                  handleMessage(message);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
    listenerThread.start();
  }

  /**
   * Метод для отключения клиента от сервера.
   *
   * @throws Exception если произошла ошибка при закрытии сокета
   */
  public void disconnect() throws Exception {
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }
  }
}
