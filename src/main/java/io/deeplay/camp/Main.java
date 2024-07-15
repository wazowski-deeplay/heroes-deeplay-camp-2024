package io.deeplay.camp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  // Пример использования логов записывающихся в главном модуле
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("root");
    System.out.println("Hi");
  }
}
