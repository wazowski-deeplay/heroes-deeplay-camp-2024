package io.deeplay.camp.core.dto.server;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferGiveUpServerDto extends ServerDto {
    private UUID gamePartyId;

    public OfferGiveUpServerDto(UUID gamePartyId){
        super(ServerDtoType.OFFER_GIVE_UP);
        this.gamePartyId = gamePartyId;
    }
}
