package io.deeplay.camp.mechanics;

import io.deeplay.camp.GameListener;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;

public interface GamePlayer extends GameListener {
  PossibleActions<Position, Position> unitsPossibleActions(GameState gameState);

  PossibleActions<Position, Unit> unitsPossiblePlacement(GameState gameState);
}
