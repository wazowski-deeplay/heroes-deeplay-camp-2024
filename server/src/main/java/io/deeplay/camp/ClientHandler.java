package io.deeplay.camp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Класс, содержащий информацию о клиенте и инструменты для общения с ним. */
public class ClientHandler {
  @Getter private UUID clientId;
  private Socket clientSocket;
  @Getter private BufferedReader reader;
  private BufferedWriter writer;

  private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  public ClientHandler(Socket clientSocket) {
    try {
      clientId = UUID.randomUUID();
      this.clientSocket = clientSocket;
      this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    } catch (IOException e) {
      logger.error("Error on reader/writer init", e);
    }
  }

  /**
   * Метод отправки сообщений для клиента.
   *
   * @param message Сообщение.
   */
  public void sendMessage(String message) {
    try {
      writer.write(message);
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      logger.error("Error sending response", e);
    }
  }

  /**
   * Метод чтения сообщений от клиента.
   *
   * @return Сообщение от клиента.
   */
  public String readRequest() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      System.out.println("Error reading request" + e.getMessage());
      return null;
    }
  }

  /** Метод закрытия ресурсов. */
  public void closeResources() {
    try {
      if (reader != null) {
        reader.close();
      }
      if (writer != null) {
        writer.close();
      }
      if (clientSocket != null) {
        clientSocket.close();
      }
    } catch (IOException e) {
      logger.error("Error closing resources", e);
    }
  }

  /**
   * Метод, проверяющий подключение клиента.
   *
   * @return true/false.
   */
  public boolean isConnected() {
    return clientSocket.isConnected();
  }
}
