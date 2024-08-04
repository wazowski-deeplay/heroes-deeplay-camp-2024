package io.deeplay.camp.game.mechanics;

import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;

public abstract class Bot {
  abstract PlaceUnitEvent generatePlaceUnitEvent(GameState gameState);

  abstract MakeMoveEvent generateMakeMoveEvent(GameState gameState);
}
