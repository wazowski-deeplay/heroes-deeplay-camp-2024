package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;

public class GetPartiesDto extends ClientDto {
  public GetPartiesDto() {
    super(ClientDtoType.GET_PARTIES);
  }
}
