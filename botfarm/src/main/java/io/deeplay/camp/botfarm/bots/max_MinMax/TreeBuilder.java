package io.deeplay.camp.botfarm.bots.max_MinMax;

import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
public class TreeBuilder {

  public static Stats buildGameTree(final GameState root, Stats stats) {
    stats.workTimeMs = System.currentTimeMillis();
    buildTreeRecursive(root, 0, stats);
    stats.workTimeMs = System.currentTimeMillis() - stats.workTimeMs;
    stats.coefBranch /= stats.numNodes;

    System.out.println("Tree construction completed.");
    System.out.println("Total number of nodes: " + stats.numNodes);
    System.out.println("Total number of terminal nodes: " + stats.numTerminalNodes);
    System.out.println("Maximum tree depth reached: " + stats.maxDepth);
    System.out.println("Average branching factor: " + stats.coefBranch);
    System.out.println("Time taken (ms): " + stats.workTimeMs);
    return stats;
  }

  @SneakyThrows
  private static void buildTreeRecursive(GameState root, int currentDepth, Stats stats) {

    List<MakeMoveEvent> makeMoveEvents = root.getPossibleMoves();
    stats.numNodes++;
    if (currentDepth == stats.maxDepth) {
      stats.numTerminalNodes++;
    } else if (makeMoveEvents.isEmpty()) {
      if (root.getGameStage() == GameStage.ENDED) {
        stats.numTerminalNodes++;
      } else {
        GameState nodeGameState = root.getCopy();
        nodeGameState.changeCurrentPlayer();
        buildTreeRecursive(nodeGameState, currentDepth + 1, stats);
      }
    }else {
        for(MakeMoveEvent makeMoveEvent : makeMoveEvents) {
            GameState nodeGameState = root.getCopy();
            nodeGameState.makeMove(makeMoveEvent);
            nodeGameState.changeCurrentPlayer();
            buildTreeRecursive(nodeGameState, currentDepth + 1, stats);
        }
    }
  }
}
