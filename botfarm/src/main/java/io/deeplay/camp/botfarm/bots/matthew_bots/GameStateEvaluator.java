package io.deeplay.camp.botfarm.bots.matthew_bots;

import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;

public interface GameStateEvaluator {
  double evaluate(GameState gameState, PlayerType maximizingPlayer);
}
