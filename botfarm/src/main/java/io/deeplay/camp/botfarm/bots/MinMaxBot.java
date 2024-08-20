package io.deeplay.camp.botfarm.bots;

import io.deeplay.camp.botfarm.bots.max_MinMax.MinMaxAlgorithm;
import io.deeplay.camp.botfarm.bots.max_MinMax.ResultFunction;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;

public class MinMaxBot {
    private final MinMaxAlgorithm minMaxAlgorithm;


    public MinMaxBot(int maxDepth, PlayerType botType) {
        this.minMaxAlgorithm = new MinMaxAlgorithm(maxDepth,new ResultFunction());
    }


    public PlaceUnitEvent generatePlaceUnitEvent(GameState gameState){
        return null;
    }


    public MakeMoveEvent generateMakeMoveEvent(GameState gameState,PlayerType playerType) {
        return minMaxAlgorithm.findBestMove(gameState, playerType);
    }
}
