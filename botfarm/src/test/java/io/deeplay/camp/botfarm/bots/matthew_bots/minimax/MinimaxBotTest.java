package io.deeplay.camp.botfarm.bots.matthew_bots.minimax;

import io.deeplay.camp.botfarm.bots.matthew_bots.BaseEvaluator;
import io.deeplay.camp.game.mechanics.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxBotTest {

    @Test
    public void minimaxTest(){
        System.out.println("DefaultMinimax:");
        GameState gameState = new GameState();
        gameState.setDefaultPlacement();
        MinimaxBot minimaxBot = new MinimaxBot(7, new BaseEvaluator());
        minimaxBot.generateMakeMoveEvent(gameState);
        minimaxBot.treeAnalyzer.printStatistics();

        System.out.println("AB Minimax:");
        gameState.setDefaultPlacement();
        AlphaBetaMinimaxBot minimaxBot1 = new AlphaBetaMinimaxBot(7, new BaseEvaluator());
        minimaxBot1.generateMakeMoveEvent(gameState);
        minimaxBot1.treeAnalyzer.printStatistics();

        System.out.println("AB multi-threaded Minimax:");
        gameState.setDefaultPlacement();
        MultiThreadMinimaxBot minimaxBot2 = new MultiThreadMinimaxBot(7, new BaseEvaluator());
        minimaxBot2.generateMakeMoveEvent(gameState);
        minimaxBot2.treeAnalyzer.printStatistics();
    }


}
