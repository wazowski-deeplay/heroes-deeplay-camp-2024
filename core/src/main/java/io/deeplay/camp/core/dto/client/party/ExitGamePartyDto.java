package io.deeplay.camp.core.dto.client.party;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExitGamePartyDto extends ClientDto {
    private UUID gamePartyId;

    public ExitGamePartyDto(UUID gamePartyId) {
        super(ClientDtoType.EXIT_PARTY);
        this.gamePartyId = gamePartyId;
    }
}
