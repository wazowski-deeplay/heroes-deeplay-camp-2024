package io.deeplay.camp.server.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.deeplay.camp.core.dto.JsonConverter;
import io.deeplay.camp.core.dto.server.GameStateDto;
import io.deeplay.camp.game.mechanics.PlayerType;
import io.deeplay.camp.server.manager.ClientManager;
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
