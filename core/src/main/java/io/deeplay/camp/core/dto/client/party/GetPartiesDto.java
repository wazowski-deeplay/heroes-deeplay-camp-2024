package io.deeplay.camp.core.dto.client.party;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;

public class GetPartiesDto extends ClientDto {
  public GetPartiesDto() {
    super(ClientDtoType.GET_PARTIES);
  }
}
