package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestartDto extends ClientDto {
  private UUID gamePartyId;

  public RestartDto(UUID gamePartyId) {
    super(ClientDtoType.RESTART);
    this.gamePartyId = gamePartyId;
  }
}
