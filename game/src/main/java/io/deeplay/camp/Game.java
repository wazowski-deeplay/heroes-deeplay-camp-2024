package io.deeplay.camp;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.mechanics.GameLogic;
import io.deeplay.camp.mechanics.GameState;

public class Game implements GameListener {

  GameState gameState;

  public Game() {
    gameState = new GameState();
  }

  @Override
  public void startGame(StartGameEvent startGameEvent) {}

  @Override
  public void placeUnit(PlaceUnitEvent placeUnitEvent) {
    if (GameLogic.isValidPlacement(gameState, placeUnitEvent)) {
      gameState.makePlacement(placeUnitEvent);
    }
  }

  @Override
  public void changePlayer(ChangePlayerEvent changePlayerEvent) {
    if (GameLogic.isValidChangePlayer(gameState, changePlayerEvent)) {
      gameState.changeCurrentPlayer();
    }
  }

  @Override
  public void makeMove(MakeMoveEvent makeMoveEvent) {
    if (GameLogic.isValidMove(gameState, makeMoveEvent)) {
      gameState.makeMove(makeMoveEvent);
    }
  }
}
