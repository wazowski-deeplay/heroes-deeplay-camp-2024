package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;
import lombok.Getter;

@Getter
public class CreatePartyRequest extends Request {
  public CreatePartyRequest() {
    super(RequestType.CREATE_PARTY);
  }
}
