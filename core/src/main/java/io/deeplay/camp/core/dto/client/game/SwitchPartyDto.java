package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SwitchPartyDto extends ClientDto {
  private UUID gamePartyId;

  public SwitchPartyDto(UUID gamePartyId) {
    super(ClientDtoType.SWITCH_PARTY);
    this.gamePartyId = gamePartyId;
  }
}
