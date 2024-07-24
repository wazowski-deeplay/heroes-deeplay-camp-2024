package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;
import lombok.Getter;

@Getter
public class MakeMoveRequest extends Request {
  public MakeMoveRequest() {
    super(RequestType.MAKE_MOVE);
  }
}
