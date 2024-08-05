package io.deeplay.camp.dto.core.client.game;

import io.deeplay.camp.dto.core.client.ClientDto;
import io.deeplay.camp.dto.core.client.ClientDtoType;
import io.deeplay.camp.game.events.GiveUpEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class GiveUpDto extends ClientDto {
    private UUID gamePartyId;

    public GiveUpDto (UUID gamePartyId){
        super(ClientDtoType.GIVE_UP);
        this.gamePartyId = gamePartyId;
    }
}
