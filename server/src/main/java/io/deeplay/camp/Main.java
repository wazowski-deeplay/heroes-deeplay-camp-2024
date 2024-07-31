package io.deeplay.camp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    // Пример использования логов записывающихся в client
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static Thread serverThread;
    private static Server server;

    public static void main(String[] args) {
        LOGGER.info("server");
        server = new Server(9090);
        serverThread = new Thread(server::start);
        serverThread.start();
    }
}