package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GiveUpDto extends ClientDto {
  private UUID gamePartyId;

  public GiveUpDto(UUID gamePartyId) {
    super(ClientDtoType.GIVE_UP);
    this.gamePartyId = gamePartyId;
  }
}
