package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.Request;
import io.deeplay.camp.dto.client.RequestType;
import lombok.Getter;

@Getter
public class PlaceUnitRequest extends Request {

  public PlaceUnitRequest() {
    super(RequestType.PLACE_UNIT);
  }
}
