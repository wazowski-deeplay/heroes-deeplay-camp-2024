package io.deeplay.camp.core.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferDrawServerDto extends ServerDto {
  private UUID gamePartyId;

  public OfferDrawServerDto(UUID gamePartyId) {
    super(ServerDtoType.OFFER_DRAW);
    this.gamePartyId = gamePartyId;
  }
}
