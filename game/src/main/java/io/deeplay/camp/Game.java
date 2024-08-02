package io.deeplay.camp;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.GiveUpEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.GameState;
import lombok.Getter;

@Getter
public class Game implements GameListener {

  private final GameState gameState;

  public Game() {
    gameState = new GameState();
  }

  @Override
  public void startGame(StartGameEvent startGameEvent) {}

  @Override
  public void placeUnit(PlaceUnitEvent placeUnitEvent) throws GameException {
    gameState.makePlacement(placeUnitEvent);
  }

  @Override
  public void changePlayer(ChangePlayerEvent changePlayerEvent) throws GameException {
    gameState.makeChangePlayer(changePlayerEvent);
  }

  @Override
  public void makeMove(MakeMoveEvent makeMoveEvent) throws GameException {
    gameState.makeMove(makeMoveEvent);
  }

  public void  giveUp(GiveUpEvent giveUpEvent) throws GameException {
    gameState.giveUp(giveUpEvent);
  }

}
