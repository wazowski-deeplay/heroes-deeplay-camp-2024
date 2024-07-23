package io.deeplay.camp.mechanics;

import io.deeplay.camp.GameListener;
import io.deeplay.camp.entities.Position;

public interface GamePlayer extends GameListener {
    PossibleActions <Position, Position> unitsPossibleActions(GameState gameState);

}
