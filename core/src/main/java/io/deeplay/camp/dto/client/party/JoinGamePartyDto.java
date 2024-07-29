package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import lombok.Getter;

@Getter
public class JoinGamePartyDto extends ClientDto {
  public JoinGamePartyDto() {
    super(ClientDtoType.JOIN_PARTY);
  }
}
