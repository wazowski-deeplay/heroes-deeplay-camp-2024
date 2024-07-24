package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;

public class ChangePlayerRequest extends Request {
  public ChangePlayerRequest() {
    super(RequestType.CHANGE_PLAYER);
  }
}
