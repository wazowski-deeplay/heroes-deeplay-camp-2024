package io.deeplay.camp.manager;

import io.deeplay.camp.ClientHandler;
import io.deeplay.camp.GameParty;
import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.server.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Класс, отвечающий за менеджмент игровых партий. */
public class GamePartyManager {
  private final Map<UUID, GameParty> gameParties;

  public GamePartyManager() {
    this.gameParties = new ConcurrentHashMap<>();
  }

  /**
   * Метод по запросу создаёт новую игровую партию и возвращает клиенту ответ.
   *
   * @param request Запрос на создание парти.
   */
  public void createParty(Request request) {}

  /**
   * Метод по запросу пытается подключить клиента к пати и возвращает ответ.
   *
   * @param request Запрос на подключение к пати.
   */
  public void joinParty(Request request) {}

  /**
   * Метод, распределяющий запросы между игровыми партиями
   *
   * @param request Запрос игровой логики.
   */
  public void delegateAction(Request request) {}
}
