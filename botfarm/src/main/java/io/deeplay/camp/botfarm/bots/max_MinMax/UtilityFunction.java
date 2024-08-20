package io.deeplay.camp.botfarm.bots.max_MinMax;

import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;

public interface UtilityFunction {
    double getUtility(GameState gameState, PlayerType playerType);
}
