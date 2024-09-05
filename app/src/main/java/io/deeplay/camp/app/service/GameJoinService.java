package io.deeplay.camp.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.app.Client;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.party.GetPartiesDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/** Сервис для общения панели с сервером. */
public class GameJoinService {
  private static final Logger logger = LoggerFactory.getLogger(GameJoinService.class);

  /** Метод, отправляющий на сервер запрос о получении списка игр. */
  public void getParties() {
    GetPartiesDto getPartiesDto = new GetPartiesDto();
    try {
      String request = JsonConverter.serialize(getPartiesDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      logger.error("Ошибка при сериализации!");

    }
  }

  /**
   * Метод, отправляющий запрос на подключение к игре.
   *
   * @param gameId id игры.
   */
  public void joinGame(UUID gameId) {
    JoinGamePartyDto joinGamePartyDto = new JoinGamePartyDto(gameId);
    try {
      String request = JsonConverter.serialize(joinGamePartyDto);
      Client.getInstance().sendMessage(request);

    } catch (JsonProcessingException e) {
      logger.error("Ошибка при сериализации!");

    }
  }
}
