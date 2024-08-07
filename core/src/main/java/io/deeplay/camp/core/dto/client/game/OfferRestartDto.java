package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferRestartDto extends ClientDto {
  private UUID gamePartyId;

  public OfferRestartDto(UUID gamePartyId) {
    super(ClientDtoType.OFFER_RESTART_GAME);
    this.gamePartyId = gamePartyId;
  }
}
