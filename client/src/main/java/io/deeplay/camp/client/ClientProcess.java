package io.deeplay.camp.client;

import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.server.*;
import io.deeplay.camp.game.mechanics.GameStage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProcess {

  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private Socket socket;
  private UUID gamePartyId;
  private BufferedReader inputUser;
  private HashMap<Integer, UUID> gamePartiesId;
  private HashMap<UUID, GameStatePlayer> gameStatesPlayer;
  private ServerHandler serverHandler;
  private ParserRequest parserRequest;
  private UserInputHandler userInputHandler;

  public ClientProcess(String addr, int port) {
    try {
      this.socket = new Socket(addr, port);
      this.serverHandler = new ServerHandler(socket);
      this.parserRequest = new ParserRequest();
      this.userInputHandler = new UserInputHandler();
      this.inputUser = new BufferedReader(new InputStreamReader(System.in));
      this.gameStatesPlayer = new HashMap<>();
      this.gamePartiesId = new HashMap<>();
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
          GameStateDto gameStateDto = (GameStateDto) serverDto;
          gameStatesPlayer.get(gameStateDto.getGamePartyId()).updateBoard(serverDto, gamePartyId);
          if (gameStatesPlayer.get(gameStateDto.getGamePartyId()).gameState.getGameStage()
              == GameStage.ENDED) {
            System.out.println("Иhumanгра окончена");
            System.out.println(
                "Хотите начать новую игру? Для этого пропишите restart или exitgame.");
          }
          return;
        case GAME_PARTY_INFO:
          GamePartyInfoDto gamePartyInfoDto = (GamePartyInfoDto) serverDto;
          if (this.gameStatesPlayer.containsKey(gamePartyInfoDto.getGamePartyId())) {
            this.gameStatesPlayer.get(gamePartyInfoDto.getGamePartyId()).playerTypeInCurrentGame =
                gamePartyInfoDto.getPlayerType();
            gameStatesPlayer.get(gamePartyInfoDto.getGamePartyId()).cleanBoard(serverDto, gamePartyId);
          } else {
            this.gameStatesPlayer.put(
                gamePartyInfoDto.getGamePartyId(),
                new GameStatePlayer(
                    gamePartyInfoDto.getGamePartyId(), gamePartyInfoDto.getPlayerType()));
          }
          gamePartyId = gamePartyInfoDto.getGamePartyId();
          System.out.println(gamePartyId);
          // Обновление инфы о текущей пати
          return;
        case GAME_PARTIES:
          GamePartiesDto gamePartiesDto = (GamePartiesDto) serverDto;
          for (int i = 0; i < gamePartiesDto.getGamePartiesIds().size(); i++) {
            gamePartiesId.put(i + 1, gamePartiesDto.getGamePartiesIds().get(i));
          }
          for (int i = 0; i < gamePartiesId.size(); i++) {
            System.out.println(i + 1 + ". " + gamePartiesId.get(i + 1));
          }
          return;
        case OFFER_DRAW:
          System.out.println("Может ничья? Пропиши draw чтобы согласиться.");
          return;
        case OFFER_RESTART_GAME:
          System.out.println("Хочешь продолжить игру? Пропиши restart чтобы согласиться.");
          return;
        case EXIT_PARTY:
          ExitPartyServerDto thisGameStateDto = (ExitPartyServerDto) serverDto;
          System.out.println(
              "Игра " + thisGameStateDto.getGamePartyId().toString() + " завершена.");
          gameStatesPlayer.get(thisGameStateDto.getGamePartyId()).downGameState();
          gameStatesPlayer.get(thisGameStateDto.getGamePartyId()).gameState = null;
          gameStatesPlayer.remove(thisGameStateDto.getGamePartyId());
          for (int i = 0; i < gamePartiesId.size(); i++) {
            if (gamePartiesId.get(i + 1) == thisGameStateDto.getGamePartyId()) {
              gamePartiesId.remove(i + 1);
              break;
            }
          }
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
          ClientDto clientDto = null;
          if(userInputHandler.isUserHandler(userWord, gamePartiesId) != null){
            gamePartyId = userInputHandler.isUserHandler(userWord, gamePartiesId);
            for (GameStatePlayer gameState : gameStatesPlayer.values()) {
              gameState.getCui().outInFrame(gameState.gameState, gameState.gamePartyId, gamePartyId);
            }
          } else if (parserRequest.convert(userWord, gamePartyId, gamePartiesId) != null) {
            clientDto = parserRequest.convert(userWord, gamePartyId, gamePartiesId);
            String sendDto = JsonConverter.serialize(clientDto);
            serverHandler.sendRequest(sendDto);
          } else {
            System.out.println("Некорректный ввод данных, попробуйте снова");
          }
        } catch (IOException e) {

          serverHandler.downService();
        }
      }
    }
  }
}
