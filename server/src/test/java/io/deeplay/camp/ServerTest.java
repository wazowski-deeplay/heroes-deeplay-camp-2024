package io.deeplay.camp;

import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.dto.server.GamePartyInfoDto;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.dto.server.ServerDto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerTest {
  private static Server server;
  private static Thread serverThread;

  private static Socket clientSocket1;
  private static BufferedReader clientIn1;
  private static BufferedWriter clientOut1;

  private static Socket clientSocket2;
  private static BufferedReader clientIn2;
  private static BufferedWriter clientOut2;

  @BeforeEach
  public void setUpServerAndClients() {
    setUpServer();
    setUpClients();
  }

  public static void setUpServer() {
    server = new Server(8080);
    serverThread = new Thread(server::start);
    serverThread.start();
  }

  public static void setUpClients() {
    try {
      clientSocket1 = new Socket("localhost", 8080);
      clientIn1 =
          new BufferedReader(
              new InputStreamReader(clientSocket1.getInputStream(), StandardCharsets.UTF_8));
      clientOut1 =
          new BufferedWriter(
              new OutputStreamWriter(clientSocket1.getOutputStream(), StandardCharsets.UTF_8));

      clientSocket2 = new Socket("localhost", 8080);
      clientIn2 =
          new BufferedReader(
              new InputStreamReader(clientSocket2.getInputStream(), StandardCharsets.UTF_8));
      clientOut2 =
          new BufferedWriter(
              new OutputStreamWriter(clientSocket2.getOutputStream(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void createHumanVsBotGamePartyTest() {
    // Отправляем запрос на создание игры против бота.
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_BOT);
    String request =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    // Получаем ответ с информацией о пати.
    String gamePartyInfo = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto gamePartyInfoDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(gamePartyInfo, ServerDto.class));
    Assertions.assertInstanceOf(GamePartyInfoDto.class, gamePartyInfoDto);

    // Получаем игровое состояние (оно отправляется, когда в пати 2 игрока, а бот добавляется
    // сразу).
    String gameState = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto gameStateDto =
        Assertions.assertDoesNotThrow(() -> JsonConverter.deserialize(gameState, ServerDto.class));
    Assertions.assertInstanceOf(GameStateDto.class, gameStateDto);
  }

  @Test
  void createHumanVsHumanGamePartyTest() {
    // Отправляем запрос на создание игры против человека.
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    String request =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    // Получаем ответ с информацией о пати(без gameState, т.к. второго игрока пока нет).
    String gamePartyInfo = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto gamePartyInfoDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(gamePartyInfo, ServerDto.class));
    Assertions.assertInstanceOf(GamePartyInfoDto.class, gamePartyInfoDto);
  }

  @Test
  void joinGamePartyTest() {
    JoinGamePartyDto joinGamePartyDto = new JoinGamePartyDto(createHumanVsHumanParty());
    String request = Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(joinGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut2, request));

    String gamePartyInfo = Assertions.assertDoesNotThrow(() -> clientIn2.readLine());
    ServerDto gamePartyInfoDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(gamePartyInfo, ServerDto.class));
    Assertions.assertInstanceOf(GamePartyInfoDto.class, gamePartyInfoDto);

    // Получаем игровое состояние (так как уже два игрока).
    String gameState = Assertions.assertDoesNotThrow(() -> clientIn2.readLine());
    ServerDto gameStateDto =
        Assertions.assertDoesNotThrow(() -> JsonConverter.deserialize(gameState, ServerDto.class));
    Assertions.assertInstanceOf(GameStateDto.class, gameStateDto);
  }

  UUID createHumanVsHumanParty() {
    // Отправляем запрос на создание игры против человека.
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    String request =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    // Получаем ответ с информацией о пати(без gameState, т.к. второго игрока пока нет).
    String gamePartyInfo = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto gamePartyInfoDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(gamePartyInfo, ServerDto.class));
    Assertions.assertInstanceOf(GamePartyInfoDto.class, gamePartyInfoDto);

    // Возвращаем id пати.
    return ((GamePartyInfoDto) gamePartyInfoDto).getGamePartyId();
  }

  void writeRequestToServer(BufferedWriter clientOut, String request) throws IOException {
    clientOut.write(request);
    clientOut.newLine();
    clientOut.flush();
  }
}
