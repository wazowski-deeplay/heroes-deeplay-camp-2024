package io.deeplay.camp.core.dto.server;

import io.deeplay.camp.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class DrawServerDto extends ServerDto{
    private UUID gamePartyId;
    public DrawServerDto(UUID gamePartyId) {
        super(ServerDtoType.DRAW);
        this.gamePartyId = gamePartyId;
    }
}
