package io.deeplay.camp.core.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OfferRestartServerDto extends ServerDto {
  private UUID gamePartyId;

  public OfferRestartServerDto(UUID gamePartyId) {
    super(ServerDtoType.OFFER_RESTART_GAME);
    this.gamePartyId = gamePartyId;
  }
}
