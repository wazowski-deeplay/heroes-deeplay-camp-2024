package io.deeplay.camp;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  // Пример использования логов записывающихся в game
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  public static void main(String[] args) {
    LOGGER.info("game");
    System.out.println("Hello, World!");
    int x = 10;
    for (int i = 0; i < x; i++) {
      System.out.println("i = " + i);
    }
  }
}
