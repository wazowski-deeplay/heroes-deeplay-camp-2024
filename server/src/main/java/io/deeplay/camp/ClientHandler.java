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
  private BufferedReader reader;
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

  public void sendMessage(String message) {
    try {
      writer.write(message);
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      logger.error("Error sending response", e);
    }
  }

  public String readRequest() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      logger.error("Error reading request", e);
      return null;
    }
  }

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
}
