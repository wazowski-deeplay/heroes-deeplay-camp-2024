package io.deeplay.camp.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.JsonConverter;
import io.deeplay.camp.dto.server.GameStateDto;
import io.deeplay.camp.manager.ClientManager;
import io.deeplay.camp.mechanics.PlayerType;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HumanPlayer extends Player {
  private UUID clientId;

  public HumanPlayer(PlayerType playerType, UUID humanPlayerId) {
    super(playerType);
    this.clientId = humanPlayerId;
  }

  public HumanPlayer(UUID clientId) {
    this.clientId = clientId;
  }

  public HumanPlayer(PlayerType playerType) {
    super(playerType);
  }

  @Override
  public void updateGameState(GameStateDto gameStateDto) {
    try {
      String gameStateDtoJson = JsonConverter.serialize(gameStateDto);
      sendMessage(gameStateDtoJson);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendMessage(String message) {
    ClientManager.getInstance().sendMessage(clientId, message);
  }
}
