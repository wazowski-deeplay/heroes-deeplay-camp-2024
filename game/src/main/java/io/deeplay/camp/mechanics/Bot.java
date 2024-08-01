package io.deeplay.camp.mechanics;

import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public abstract class Bot {
  abstract PlaceUnitEvent generatePlaceUnitEvent(GameState gameState);

  abstract MakeMoveEvent generateMakeMoveEvent(GameState gameState);
}
