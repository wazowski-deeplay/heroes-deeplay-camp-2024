package io.deeplay.camp.botfarm.bots.matthew_bots;

public class TreeAnalyzer {
  private int nodesCount = 0;
  private long moveStartTime = 0;
  private long moveEndTime = 0;

  public void startMoveStopWatch() {
    moveStartTime = System.currentTimeMillis();
  }

  public void endMoveStopWatch() {
    moveEndTime = System.currentTimeMillis();
  }

  public long getMoveTime() {
    return moveEndTime - moveStartTime;
  }

  public void incrementNodesCount() {
    nodesCount++;
  }

  public void resetTreeAnalyzer() {
    nodesCount = 0;
  }

  public void printStatistics() {
    System.out.println("Tree Analysis Statistics:");
    System.out.println("------------------------");
    System.out.println("Nodes Visited: " + nodesCount);
    System.out.println("Move Time: " + getMoveTime() + " ms");
    System.out.println("------------------------");
  }
}
