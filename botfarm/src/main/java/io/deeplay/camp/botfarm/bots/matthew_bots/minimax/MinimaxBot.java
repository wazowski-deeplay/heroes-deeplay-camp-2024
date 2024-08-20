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
import lombok.SneakyThrows;

public class MinimaxBot extends Bot {
  private PlayerType maximizingPlayerType;

  private final int maxDepth;
  final TreeAnalyzer treeAnalyzer;
  private static final double MAX_COST = Double.POSITIVE_INFINITY;
  private static final double MIN_COST = Double.NEGATIVE_INFINITY;

  private final GameStateEvaluator gameStateEvaluator;

  public MinimaxBot(int depth, GameStateEvaluator gameStateEvaluator) {
    maxDepth = depth;
    this.gameStateEvaluator = gameStateEvaluator;
    treeAnalyzer = new TreeAnalyzer();
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
    MinimaxResult result = minimax(gameState, maxDepth, true);
    treeAnalyzer.endMoveStopWatch();

    return (MakeMoveEvent) result.event;
  }

  private MinimaxResult minimax(GameState gameState, int depth, boolean maximizing)
          throws GameException {
    treeAnalyzer.incrementNodesCount();
    // Базовый случай (Дошли до ограничения глубины или конца игры)
    if (depth == 0 || gameState.getGameStage() == GameStage.ENDED) {
      return new MinimaxResult(null, gameStateEvaluator.evaluate(gameState, maximizingPlayerType));
    }

    List<MakeMoveEvent> possibleMoves = gameState.getPossibleMoves();
    if (possibleMoves.isEmpty()) {
      GameState newGameState = gameState.getCopy();
      newGameState.changeCurrentPlayer();
      return minimax(newGameState, depth - 1, !maximizing);
    }

    return getBestResult(gameState, depth, possibleMoves, maximizing);
  }

  @SneakyThrows
  private MinimaxResult getBestResult(
          GameState gameState, int depth, List<MakeMoveEvent> possibleMoves, boolean maximizing) throws GameException {
    MinimaxResult bestResult = new MinimaxResult(possibleMoves.getFirst(), maximizing ? MIN_COST : MAX_COST);
    for (MakeMoveEvent move : possibleMoves) {
      GameState newGameState = gameState.getCopy();
      newGameState.makeMove(move);
      MinimaxResult result = minimax(newGameState, depth - 1, maximizing);
      if (maximizing && result.score > bestResult.score || !maximizing && result.score < bestResult.score) {
        bestResult = new MinimaxResult(move, result.score);
      }
    }
    return bestResult;
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