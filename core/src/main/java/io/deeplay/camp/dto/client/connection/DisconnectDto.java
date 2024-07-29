package io.deeplay.camp.dto.client.connection;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;

public class DisconnectDto extends ClientDto {
  public DisconnectDto() {
    super(ClientDtoType.DISCONNECT);
  }
}
