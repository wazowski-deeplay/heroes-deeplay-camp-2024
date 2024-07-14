package io.deeplay.camp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  // Пример использования логов записывающихся в server
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("server");
    System.out.println("Hello world!");
  }
}
