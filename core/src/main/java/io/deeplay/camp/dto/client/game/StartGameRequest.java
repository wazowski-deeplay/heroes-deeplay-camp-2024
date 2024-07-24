package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;

public class StartGameRequest extends Request {
  public StartGameRequest() {
    super(RequestType.START_GAME);
  }
}
