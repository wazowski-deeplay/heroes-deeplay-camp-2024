package io.deeplay.camp.player;

import io.deeplay.camp.mechanics.PlayerType;
import java.util.HashMap;
import java.util.Map;

public class Players {
  Map<PlayerType, Player> hashMap;

  public Players() {
    hashMap = new HashMap<>();
  }

  public void addPlayer(PlayerType playerType, Player player) {
    hashMap.put(playerType, player);
  }

  public boolean isFull() {
    return hashMap.size() == 2;
  }

  public void notifyPlayers(String message) {
    for (Player player : hashMap.values()) {}
  }
}
