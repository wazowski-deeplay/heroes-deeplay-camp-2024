package io.deeplay.camp.core.dto.client.connection;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DisconnectDto extends ClientDto {
  private UUID gamePartyId;

  public DisconnectDto(UUID gamePartyId) {
    super(ClientDtoType.DISCONNECT);
    this.gamePartyId = gamePartyId;
  }
}
