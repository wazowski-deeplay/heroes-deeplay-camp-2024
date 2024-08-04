package io.deeplay.camp.core.dto.client.connection;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;

public class DisconnectDto extends ClientDto {
  public DisconnectDto() {
    super(ClientDtoType.DISCONNECT);
  }
}
