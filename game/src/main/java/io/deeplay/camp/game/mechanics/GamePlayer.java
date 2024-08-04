package io.deeplay.camp.game.mechanics;

import io.deeplay.camp.game.GameListener;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;

public interface GamePlayer extends GameListener {
  PossibleActions<Position, Position> unitsPossibleActions(GameState gameState);

  PossibleActions<Position, Unit> unitsPossiblePlacement(GameState gameState);
}
