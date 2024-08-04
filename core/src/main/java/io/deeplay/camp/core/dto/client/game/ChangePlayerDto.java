package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChangePlayerDto extends ClientDto {
  private UUID gamePartyId;

  public ChangePlayerDto(UUID gamePartyId) {
    super(ClientDtoType.CHANGE_PLAYER);
    this.gamePartyId = gamePartyId;
  }
}
