package io.deeplay.camp.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler {
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  // Принимающий с сервера
  private final BufferedReader reader;
  // Отдающий на сервер
  private final BufferedWriter writer;
  private final Socket socket;
  // Чтение из консоли
  private BufferedReader inputUser;

  public ServerHandler(Socket socket) {
    this.socket = socket;
    try {
      this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendRequest(String message) {
    try {
      writer.write(message);
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      logger.error("Error sending response", e);
    }
  }

  public String readResponse() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      System.out.println("Error reading response" + e.getMessage());
      return null;
    }
  }

  void downService() {
    try {
      if (!socket.isClosed()) {
        socket.close();
        reader.close();
        writer.close();
      }
    } catch (IOException ignored) {
      System.out.println("Server exception");
    }
  }
}
