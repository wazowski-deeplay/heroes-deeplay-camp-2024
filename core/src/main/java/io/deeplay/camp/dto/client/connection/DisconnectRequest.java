package io.deeplay.camp.dto.client.connection;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;

public class DisconnectRequest extends Request {
  public DisconnectRequest() {
    super(RequestType.DISCONNECT);
  }
}
