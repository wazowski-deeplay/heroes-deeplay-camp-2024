package io.deeplay.camp.dto.server;

import io.deeplay.camp.mechanics.GameState;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameStateDto extends ServerDto {
    private UUID gamePartyId;
    private GameState gameState;
    public GameStateDto(UUID gamePartyId, GameState gameState) {
        super(ServerDtoType.GAME_STATE);
        this.gamePartyId = gamePartyId;
        this.gameState = gameState;
    }
}
