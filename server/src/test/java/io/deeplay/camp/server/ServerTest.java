package io.deeplay.camp.server;

import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.core.dto.client.party.GetPartiesDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.core.dto.server.ConnectionErrorCode;
import io.deeplay.camp.core.dto.server.ErrorConnectionResponseDto;
import io.deeplay.camp.core.dto.server.GamePartiesDto;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerTest {

  private static BufferedReader clientIn1;
  private static BufferedWriter clientOut1;

  private static BufferedReader clientIn2;
  private static BufferedWriter clientOut2;

  private static BufferedReader clientIn3;
  private static BufferedWriter clientOut3;

  public static void setUpServer() {
    Server server = new Server(8080);
    Thread serverThread = new Thread(server::start);
    serverThread.start();
  }

  public static void setUpClients() {
    try {
      Socket clientSocket1 = new Socket("localhost", 8080);
      clientIn1 =
          new BufferedReader(
              new InputStreamReader(clientSocket1.getInputStream(), StandardCharsets.UTF_8));
      clientOut1 =
          new BufferedWriter(
              new OutputStreamWriter(clientSocket1.getOutputStream(), StandardCharsets.UTF_8));

      Socket clientSocket2 = new Socket("localhost", 8080);
      clientIn2 =
          new BufferedReader(
              new InputStreamReader(clientSocket2.getInputStream(), StandardCharsets.UTF_8));
      clientOut2 =
          new BufferedWriter(
              new OutputStreamWriter(clientSocket2.getOutputStream(), StandardCharsets.UTF_8));
      Socket clientSocket3 = new Socket("localhost", 8080);
      clientIn3 =
          new BufferedReader(
              new InputStreamReader(clientSocket3.getInputStream(), StandardCharsets.UTF_8));
      clientOut3 =
          new BufferedWriter(
              new OutputStreamWriter(clientSocket3.getOutputStream(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setUpServerAndClients() {
    setUpServer();
    setUpClients();
  }

  @AfterEach
  void tearDown() throws IOException {
    clientIn1.close();
    clientOut1.close();
    clientIn2.close();
    clientOut2.close();
    clientIn3.close();
    clientOut3.close();
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
  void createGamePartyWithInvalidGameTypeTest() {
    CreateGamePartyDto invalidDto = new CreateGamePartyDto(null);
    String request = Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(invalidDto));

    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    String errorResponse = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto errorDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(errorResponse, ServerDto.class));
    Assertions.assertInstanceOf(ErrorConnectionResponseDto.class, errorDto);
    Assertions.assertEquals(
        ConnectionErrorCode.UNIDENTIFIED_ERROR,
        ((ErrorConnectionResponseDto) errorDto).getConnectionErrorCode());
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

  @Test
  void joinNonExistentGamePartyTest() {
    UUID nonExistentGamePartyId = UUID.randomUUID();
    JoinGamePartyDto joinGamePartyDto = new JoinGamePartyDto(nonExistentGamePartyId);
    String request = Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(joinGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    // Ожидаем, что сервер вернет сообщение об ошибке
    String errorResponse = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto errorDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(errorResponse, ServerDto.class));

    // Проверяем, что сервер вернул объект ErrorResponseDto с соответствующим кодом ошибки
    Assertions.assertInstanceOf(ErrorConnectionResponseDto.class, errorDto);
    Assertions.assertEquals(
        ConnectionErrorCode.NON_EXISTENT_CONNECTION,
        ((ErrorConnectionResponseDto) errorDto).getConnectionErrorCode());
  }

  @Test
  void joinGamePartyTest_IsNotValid() {
    // Попытка присоединиться к полной игре
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    String request =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    // Получаем ответ с информацией о пати (без gameState, т.к. второго игрока пока нет)
    String gamePartyInfo = Assertions.assertDoesNotThrow(() -> clientIn1.readLine());
    ServerDto gamePartyInfoDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(gamePartyInfo, ServerDto.class));
    UUID gamePartyId = ((GamePartyInfoDto) gamePartyInfoDto).getGamePartyId();

    // Присоединяем второго игрока, чтобы сделать игру полной
    JoinGamePartyDto joinGamePartyDto1 = new JoinGamePartyDto(gamePartyId);
    String joinRequest1 =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(joinGamePartyDto1));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut2, joinRequest1));

    // Получаем подтверждение о присоединении второго игрока
    String joinResponse = Assertions.assertDoesNotThrow(() -> clientIn2.readLine());
    ServerDto joinResponseDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(joinResponse, ServerDto.class));
    Assertions.assertInstanceOf(GamePartyInfoDto.class, joinResponseDto);

    JoinGamePartyDto joinGamePartyDto2 = new JoinGamePartyDto(gamePartyId);
    String joinRequest2 =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(joinGamePartyDto2));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut3, joinRequest2));

    String errorResponse = Assertions.assertDoesNotThrow(() -> clientIn3.readLine());
    ServerDto errorDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(errorResponse, ServerDto.class));

    // Проверяем, что сервер вернул объект ErrorResponseDto с соответствующим кодом ошибки
    Assertions.assertInstanceOf(ErrorConnectionResponseDto.class, errorDto);
    Assertions.assertEquals(
        ConnectionErrorCode.FULL_PARTY,
        ((ErrorConnectionResponseDto) errorDto).getConnectionErrorCode());
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

  @Test
  void getPartiesTest() throws InterruptedException {
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    String request =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut1, request));

    CreateGamePartyDto createGamePartyDto2 = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    String request2 =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(createGamePartyDto2));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut2, request2));

    Thread.sleep(10000);
    GetPartiesDto getPartiesDto = new GetPartiesDto();
    String getPartiesDtoString =
        Assertions.assertDoesNotThrow(() -> JsonConverter.serialize(getPartiesDto));
    Assertions.assertDoesNotThrow(() -> writeRequestToServer(clientOut3, getPartiesDtoString));

    String responseString = Assertions.assertDoesNotThrow(() -> clientIn3.readLine());
    ServerDto serverDto =
        Assertions.assertDoesNotThrow(
            () -> JsonConverter.deserialize(responseString, ServerDto.class));
    Assertions.assertInstanceOf(GamePartiesDto.class, serverDto);
    GamePartiesDto gamePartiesDto = (GamePartiesDto) serverDto;
    Assertions.assertEquals(2, gamePartiesDto.getGamePartiesIds().size());
  }
}
