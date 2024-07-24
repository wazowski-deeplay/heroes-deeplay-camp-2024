package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.ChangePlayerRequest;
import io.deeplay.camp.dto.client.game.MakeMoveRequest;
import io.deeplay.camp.dto.client.game.PlaceUnitRequest;
import io.deeplay.camp.dto.client.game.StartGameRequest;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;

public class RequestToEventConverter {

  public static PlaceUnitEvent convert(PlaceUnitRequest placeUnitRequest) {
    return null;
  }

  public static MakeMoveEvent convert(MakeMoveRequest makeMoveRequest) {
    return null;
  }

  public static StartGameEvent convert(StartGameRequest startGameRequest) {
    return null;
  }

  public static ChangePlayerEvent convert(ChangePlayerRequest changePlayerRequest) {
    return null;
  }
}
