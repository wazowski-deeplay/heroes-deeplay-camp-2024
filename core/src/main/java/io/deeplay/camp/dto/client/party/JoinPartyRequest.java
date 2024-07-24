package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;
import lombok.Getter;

@Getter
public class JoinPartyRequest extends Request {
  public JoinPartyRequest() {
    super(RequestType.JOIN_PARTY);
  }
}
