package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
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
