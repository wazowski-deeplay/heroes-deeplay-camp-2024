package io.deeplay.camp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("server");
    Server server = new Server(9090);
    Thread serverThread = new Thread(server::start);
    serverThread.start();
  }
}
