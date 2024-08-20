package io.deeplay.camp.botfarm.bots.matthew_bots.minimax;

import io.deeplay.camp.botfarm.bots.Bot;
import io.deeplay.camp.botfarm.bots.matthew_bots.GameStateEvaluator;
import io.deeplay.camp.botfarm.bots.matthew_bots.TreeAnalyzer;
import io.deeplay.camp.game.events.Event;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import lombok.SneakyThrows;

public class MultiThreadMinimaxBot extends Bot {
  private PlayerType maximizingPlayerType;
  final TreeAnalyzer treeAnalyzer;
  private final int maxDepth;
  private static final double MAX_COST = Double.POSITIVE_INFINITY;
  private static final double MIN_COST = Double.NEGATIVE_INFINITY;

  private final GameStateEvaluator gameStateEvaluator;
  private final ForkJoinPool forkJoinPool = new ForkJoinPool();

  public MultiThreadMinimaxBot(int depth, GameStateEvaluator gameStateEvaluator) {
    maxDepth = depth;
    this.gameStateEvaluator = gameStateEvaluator;
    this.treeAnalyzer = new TreeAnalyzer();
  }

  @Override
  public PlaceUnitEvent generatePlaceUnitEvent(GameState gameState) {
    List<PlaceUnitEvent> placeUnitEvents = gameState.getPossiblePlaces();
    Random random = new Random();
    int randomIndex = random.nextInt(placeUnitEvents.size());
    return placeUnitEvents.get(randomIndex);
  }

  @Override
  @SneakyThrows
  public MakeMoveEvent generateMakeMoveEvent(GameState gameState) {
    maximizingPlayerType = gameState.getCurrentPlayer();
    treeAnalyzer.startMoveStopWatch();
    MinimaxTask task = new MinimaxTask(gameState, maxDepth, true, MIN_COST, MAX_COST);
    MinimaxResult result = forkJoinPool.invoke(task);
    treeAnalyzer.endMoveStopWatch();
    return (MakeMoveEvent) result.event;
  }

  private class MinimaxTask extends RecursiveTask<MinimaxResult> {
    private final GameState gameState;
    private final int depth;
    private final boolean maximizing;
    private double alpha;
    private double beta;

    public MinimaxTask(
            GameState gameState, int depth, boolean maximizing, double alpha, double beta) {
      this.gameState = gameState;
      this.depth = depth;
      this.maximizing = maximizing;
      this.alpha = alpha;
      this.beta = beta;
    }

    @Override
    protected MinimaxResult compute() {
      treeAnalyzer.incrementNodesCount();
      // Базовый случай (Дошли до ограничения глубины или конца игры)
      if (depth == 0 || gameState.getGameStage() == GameStage.ENDED) {
        return new MinimaxResult(
                null, gameStateEvaluator.evaluate(gameState, maximizingPlayerType));
      }

      try {
        List<MakeMoveEvent> possibleMoves = gameState.getPossibleMoves();
        if (possibleMoves.isEmpty()) {
          GameState newGameState = gameState.getCopy();
          gameState.changeCurrentPlayer();
          MinimaxTask task = new MinimaxTask(newGameState, depth - 1, !maximizing, alpha, beta);
          return task.compute();
        }

        return getBestResult(possibleMoves);

      } catch (GameException e) {
        System.out.println("MinimaxBot Error");
        return new MinimaxResult(null, 0);
      }
    }

    @SneakyThrows
    private MinimaxResult getBestResult(List<MakeMoveEvent> possibleMoves)
            throws GameException {
      MinimaxResult bestResult = new MinimaxResult(possibleMoves.getFirst(), maximizing ? MIN_COST : MAX_COST);
      for (MakeMoveEvent move : possibleMoves) {
        GameState newGameState = gameState.getCopy();
        newGameState.makeMove(move);
        MinimaxTask task = new MinimaxTask(newGameState, depth - 1, maximizing, alpha, beta);
        MinimaxResult result = task.compute();
        if (maximizing && result.score > bestResult.score || !maximizing && result.score < bestResult.score) {
          bestResult = new MinimaxResult(move, result.score);
        }
        if (maximizing) {
          alpha = Math.max(alpha, bestResult.score);
        } else {
          beta = Math.min(beta, bestResult.score);
        }
        if (beta <= alpha) {
          break;
        }
      }
      return bestResult;
    }
  }

  private static class MinimaxResult {
    Event event;
    double score;

    MinimaxResult(Event event, double score) {
      this.event = event;
      this.score = score;
    }
  }
}