package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferDrawDto extends ClientDto {
  private UUID gamePartyId;

  public OfferDrawDto(UUID gamePartyId) {
    super(ClientDtoType.OFFER_DRAW);
    this.gamePartyId = gamePartyId;
  }
}
