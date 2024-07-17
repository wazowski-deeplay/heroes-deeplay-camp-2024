package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameState {

  private Board board;

  private PlayerType currentPlayer;

  private GameStage gameStage;

  public GameState() {
    board = new Board();
    currentPlayer = PlayerType.FIRST_PLAYER;
    gameStage = GameStage.PLACEMENT_STAGE;
  }

  public void changeCurrentPlayer() {
    if (currentPlayer == PlayerType.FIRST_PLAYER) {
      currentPlayer = PlayerType.SECOND_PLAYER;
    } else {
      currentPlayer = PlayerType.FIRST_PLAYER;
    }
  }

  // методы чисто для применения, проверка происходит до их использования
  public void makeMove(MakeMoveEvent move) {
    // что-то вроде достать атакующего и атакуемого юнита и сделать attacker.playMove(attacked)
  }

  public void makePlacement(PlaceUnitEvent placeUnit) {}
}
