package io.deeplay.camp.mechanics;

public enum PlayerType {
  FIRST_PLAYER,
  SECOND_PLAYER;

  public PlayerType switchPlayer() {
    if (this == FIRST_PLAYER) {
      return SECOND_PLAYER;
    } else {
      return FIRST_PLAYER;
    }
  }
}
