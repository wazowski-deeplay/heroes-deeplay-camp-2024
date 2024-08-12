package io.deeplay.camp.botfarm.bots;

import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.mechanics.GameState;

public abstract class Bot {

    protected abstract PlaceUnitEvent generatePlaceUnitEvent(GameState gameState);

    protected abstract MakeMoveEvent generateMakeMoveEvent(GameState gameState);
}
