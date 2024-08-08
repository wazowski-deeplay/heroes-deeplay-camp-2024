package io.deeplay.camp.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.app.Client;
import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import lombok.Setter;

/** Сервис для общения панели с сервером. */
@Setter
public class GameConfigService {

  /** Метод, посылающий запрос на сервер о создании игры против бота. */
  public void createGameVsBot() {
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_BOT);
    try {
      String request = JsonConverter.serialize(createGamePartyDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
    }
  }

  /** Метод, посылающий запрос на сервер о создании игры против человека. */
  public void createGameVsHuman() {
    CreateGamePartyDto createGamePartyDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
    try {
      String request = JsonConverter.serialize(createGamePartyDto);
      Client.getInstance().sendMessage(request);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
    }
  }
}
