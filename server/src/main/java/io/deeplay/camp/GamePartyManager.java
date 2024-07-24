package io.deeplay.camp;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.party.CreatePartyRequest;
import io.deeplay.camp.dto.server.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Класс, отвечающий за менеджмент игровых партий. */
public class GamePartyManager {
  private final ConcurrentHashMap<Long, GameParty> gameParties;
  private final AtomicLong gameIdCounter;

  public GamePartyManager(
      ConcurrentHashMap<Long, GameParty> gameParties, AtomicLong gameIdCounter) {
    this.gameParties = gameParties;
    this.gameIdCounter = gameIdCounter;
  }

  /**
   * Метод по запросу создаёт новую игровую партию и возвращает клиенту ответ.
   *
   * @param request Запрос на создание парти.
   * @return Отчёт о создании.
   */
  public Response createParty(Request request) {
    return null;
  }

  /**
   * Метод по запросу пытается подключить клиента к пати и возвращает ответ.
   *
   * @param request Запрос на подключение к пати.
   * @return Отчёт о подключении.
   */
  public Response joinParty(Request request) {
    return null;
  }

  /**
   * Метод, распределяющий запросы между игровыми партиями
   *
   * @param request Запрос игровой логики.
   * @return Ответ на действие игрока.
   */
  public Response delegateAction(Request request) {
    return null;
  }
}
