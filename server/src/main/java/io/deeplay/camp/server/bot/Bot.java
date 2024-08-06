package io.deeplay.camp.server.bot;

import io.deeplay.camp.game.events.DrawEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.mechanics.GameState;

public abstract class Bot {
  public abstract PlaceUnitEvent generatePlaceUnitEvent(GameState gameState);

  public abstract MakeMoveEvent generateMakeMoveEvent(GameState gameState);

  public abstract DrawEvent generateDrawEvent(GameState gameState);
}
