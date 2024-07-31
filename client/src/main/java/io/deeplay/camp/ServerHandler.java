package io.deeplay.camp;

import java.io.*;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler {
  // Принимающий с сервера
  private BufferedReader reader;
  // Отдающий на сервер
  private BufferedWriter writer;
  // Чтение из консоли
  private BufferedReader inputUser;
  private Socket socket;
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

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
    }
  }
}
