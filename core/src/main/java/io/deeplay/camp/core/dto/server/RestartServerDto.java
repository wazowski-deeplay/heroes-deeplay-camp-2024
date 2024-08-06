package io.deeplay.camp.core.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RestartServerDto extends ServerDto {
    private UUID gamePartyId;

    public RestartServerDto(UUID gamePartyId) {
        super(ServerDtoType.RESTART);
        this.gamePartyId = gamePartyId;
    }
}
