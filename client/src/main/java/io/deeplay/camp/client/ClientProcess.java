package io.deeplay.camp.client;

import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.server.ErrorConnectionResponseDto;
import io.deeplay.camp.core.dto.server.ErrorGameResponseDto;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.core.dto.server.ServerDto;
import io.deeplay.camp.game.mechanics.GameStage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProcess {

  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private Socket socket;
  private UUID gamePartyId;
  private GameStatePlayer gameStatePlayer;
  private BufferedReader inputUser;
  private ServerHandler serverHandler;
  private ParserRequest parserRequest;

  public ClientProcess(String addr, int port) {
    try {
      this.socket = new Socket(addr, port);
      this.serverHandler = new ServerHandler(socket);
      this.gameStatePlayer = new GameStatePlayer();
      this.parserRequest = new ParserRequest();
      this.inputUser = new BufferedReader(new InputStreamReader(System.in));
    } catch (IOException e) {
      System.err.println("Socket failed");
    }
    new ReadResponse().start();
    new WriteRequest().start();
  }

  private void handleResponse(ServerDto serverDto) {
    try {
      switch (serverDto.getServerDtoType()) {
        case GAME_STATE:
          gameStatePlayer.updateBoard(serverDto);
          if (gameStatePlayer.gameState.getGameStage() == GameStage.ENDED) {
            System.out.println("Игра окончена");
          }
          // Обновление доски
          return;
        case GAME_PARTY_INFO:
          GamePartyInfoDto gamePartyInfoDto = (GamePartyInfoDto) serverDto;
          gamePartyId = gamePartyInfoDto.getGamePartyId();
          System.out.println(gamePartyId);
          // Обновление инфы о текущей пати
          return;
        case OFFER_DRAW:
          System.out.println("Может ничья? Пропиши draw чтобы согласиться.");
          return;
        case OFFER_CONTINUE_GAME:
          System.out.println("Хочешь продолжить игру?");
          return;
        case ERROR_CONNECTION_INFO:
          ErrorConnectionResponseDto errorConnectionResponseDto =
              (ErrorConnectionResponseDto) serverDto;
          System.out.println(errorConnectionResponseDto.getConnectionErrorCode());
          return;
        case ERROR_GAME_INFO:
          ErrorGameResponseDto errorGameResponseDto = (ErrorGameResponseDto) serverDto;
          System.out.println(errorGameResponseDto.getMessage());
          return;
        default:
          return;
      }
    } catch (Exception e) {
      logger.error("Server error", e);
    }
  }

  private class ReadResponse extends Thread {
    @Override
    public void run() {
      try {
        String responseJson;
        while ((responseJson = serverHandler.readResponse()) != null) {
          ServerDto serverDto = JsonConverter.deserialize(responseJson, ServerDto.class);
          handleResponse(serverDto);
        }
      } catch (Exception e) {
        logger.error("Session error", e);
      } finally {
        logger.info("Session closed");
        serverHandler.downService();
      }
    }
  }

  public class WriteRequest extends Thread {
    @Override
    public void run() {
      while (true) {
        String userWord;
        try {
          userWord = inputUser.readLine();
          ClientDto clientDto;
          if (parserRequest.convert(userWord, gamePartyId) != null) {
            clientDto = parserRequest.convert(userWord, gamePartyId);
          } else {
            System.out.println("Некорректный ввод данных, попробуйте снова");
            continue;
          }
          String sendDto = JsonConverter.serialize(clientDto);
          serverHandler.sendRequest(sendDto);
        } catch (IOException e) {

          serverHandler.downService();
        }
      }
    }
  }
}
