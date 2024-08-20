package io.deeplay.camp.botfarm;

import io.deeplay.camp.botfarm.bots.max_MinMax.ResultFunction;
import io.deeplay.camp.botfarm.bots.max_MinMax.Stats;
import io.deeplay.camp.botfarm.bots.max_MinMax.TreeBuilder;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TreeBuilderTest {
  private GameState gameState;

  @BeforeEach
  public void setUp() {
    gameState = new GameState();
    gameState.setDefaultPlacement();
  }

  @Test
  void testTreeBuilder() {
    Stats stats = new Stats();
    TreeBuilder.buildGameTree(gameState, stats);
    Assertions.assertEquals(stats.getNumTerminalNodes(), 17);
  }

  @Test
  void testTreeBuilderMaxDepth() {
    Stats stats = new Stats(2);
    // first
    gameState.getBoard().getUnit(0, 0).setCurrentHp(0);
    gameState.getBoard().getUnit(1, 0).setCurrentHp(0);
    gameState.getBoard().getUnit(2, 0).setCurrentHp(0);
    gameState.getBoard().getUnit(0, 1).setCurrentHp(0);
    gameState.getBoard().getUnit(2, 1).setCurrentHp(0);
    // second
    gameState.getBoard().getUnit(0, 2).setCurrentHp(0);
    gameState.getBoard().getUnit(2, 2).setCurrentHp(0);
    gameState.getBoard().getUnit(0, 3).setCurrentHp(0);
    gameState.getBoard().getUnit(1, 3).setCurrentHp(0);
    gameState.getBoard().getUnit(2, 3).setCurrentHp(0);

    TreeBuilder.buildGameTree(gameState, stats);
    Assertions.assertEquals(stats.getNumTerminalNodes(), 1);
    Assertions.assertEquals(stats.getNumNodes(), 3);

  }

  @Test
  void testResultFunction() {
    ResultFunction resultFunction = new ResultFunction();
    double a = resultFunction.getUtility(gameState, PlayerType.FIRST_PLAYER);
    Assertions.assertEquals(a, 0);
  }

  @Test
  void testResultFunction1() {
    ResultFunction resultFunction = new ResultFunction();
    gameState.getBoard().getUnit(1, 2).setCurrentHp(0);
    gameState.changeCurrentPlayer();
    double a = resultFunction.getUtility(gameState, PlayerType.SECOND_PLAYER);
    Assertions.assertEquals(a, -15);
    gameState.changeCurrentPlayer();
    double b = resultFunction.getUtility(gameState, PlayerType.FIRST_PLAYER);
    Assertions.assertEquals(b, 15);
  }


}
