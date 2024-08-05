package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class OfferGiveUpDto extends ClientDto {
    private UUID gamePartyId;

    public OfferGiveUpDto(UUID gamePartyId){
        super(ClientDtoType.OFFER_GIVE_UP);
        this.gamePartyId = gamePartyId;
    }
}
