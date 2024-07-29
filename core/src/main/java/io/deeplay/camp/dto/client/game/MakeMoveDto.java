package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MakeMoveDto extends ClientDto {
  private UUID gamePartyId;

  public MakeMoveDto(UUID gamePartyId) {
    super(ClientDtoType.MAKE_MOVE);
    this.gamePartyId = gamePartyId;
  }
}
