package io.deeplay.camp.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.app.Client;
import io.deeplay.camp.app.controller.GameController;
import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Сервис для общения панели с сервером. */
@Setter
public class GameConfigService {
  private static final Logger logger = LoggerFactory.getLogger(GameConfigService.class);

  /** Метод, посылающий запрос на сервер о создании игры против бота. */
  public void createGameVsBot() {
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_BOT);
    try {
      String request = JsonConverter.serialize(createGamePartyDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      logger.error("Ошибка при сериализации!");
    }
  }

  /** Метод, посылающий запрос на сервер о создании игры против человека. */
  public void createGameVsHuman() {
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    try {
      String request = JsonConverter.serialize(createGamePartyDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      logger.error("Ошибка при сериализации!");

    }
  }
}
