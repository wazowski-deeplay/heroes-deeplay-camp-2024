package io.deeplay.camp.game.mechanics;

public enum PlayerType {
  FIRST_PLAYER,
  SECOND_PLAYER,
  DRAW;

  public PlayerType switchPlayer() {
    if (this == FIRST_PLAYER) {
      return SECOND_PLAYER;
    } else {
      return FIRST_PLAYER;
    }
  }
}
