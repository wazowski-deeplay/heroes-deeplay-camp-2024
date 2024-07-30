package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class JoinGamePartyDto extends ClientDto {
  private UUID gamePartyId;

  public JoinGamePartyDto(UUID gamePartyId) {
    super(ClientDtoType.JOIN_PARTY);
    this.gamePartyId = gamePartyId;
  }
}
