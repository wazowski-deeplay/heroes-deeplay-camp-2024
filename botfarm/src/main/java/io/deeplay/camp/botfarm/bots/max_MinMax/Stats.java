package io.deeplay.camp.botfarm.bots.max_MinMax;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Stats {
    int numNodes;
    int numTerminalNodes;
    int maxDepth = 1;
    double coefBranch;
    long workTimeMs;

    public Stats(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
