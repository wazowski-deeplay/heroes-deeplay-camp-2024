package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlaceUnitDto extends ClientDto {
  private UUID gamePartyId;

  public PlaceUnitDto(UUID gamePartyId) {
    super(ClientDtoType.PLACE_UNIT);
    this.gamePartyId = gamePartyId;
  }
}
