package io.deeplay.camp;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.server.GamePartyInfoDto;
import io.deeplay.camp.dto.server.ServerDto;
import java.io.*;
import java.net.Socket;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProcess {

  private Socket socket;
  private String addr;
  private int port;

  private UUID gamePartyId;
  private GameStatePlayer gameStatePlayer;
  private BufferedReader inputUser;
  private ServerHandler serverHandler;
  private ParserRequest parserRequest;
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

  public ClientProcess(String addr, int port) {
    //this.addr = addr;
    this.port = port;
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

  private void handleResponse(ServerDto serverDto) {
    try {
      switch (serverDto.getServerDtoType()) {
        case GAME_STATE:
          gameStatePlayer.updateBoard(serverDto);
          // Обновление доски
          return;
        case GAME_PARTY_INFO:
          GamePartyInfoDto gamePartyInfoDto = (GamePartyInfoDto) serverDto;
          gamePartyId = gamePartyInfoDto.getGamePartyId();
          System.out.println(gamePartyId);
          // Обновление инфы о текущей пати
          return;
        default:
          return;
      }
    } catch (Exception e) {

    }
  }
}
