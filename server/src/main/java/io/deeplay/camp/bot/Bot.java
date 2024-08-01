package io.deeplay.camp.bot;

import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.mechanics.GameState;

public abstract class Bot {
  public abstract PlaceUnitEvent generatePlaceUnitEvent(GameState gameState);

  public abstract MakeMoveEvent generateMakeMoveEvent(GameState gameState);
}
