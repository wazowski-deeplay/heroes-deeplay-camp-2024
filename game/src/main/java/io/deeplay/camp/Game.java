package io.deeplay.camp;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.mechanics.GameLogic;
import io.deeplay.camp.mechanics.GameState;

public class Game implements GameListener {

  GameState gameState;

  public Game() {
    gameState = new GameState();
  }

  @Override
  public void startGame(StartGameEvent startGameEvent) {}

  // Пока не сказанно что ход последний можно сколько угодно переставлять фигуры
  @Override
  public void placeUnit(PlaceUnitEvent placeUnitEvent) throws GameException {
      gameState.makePlacement(placeUnitEvent);
  }

  @Override
  public void changePlayer(ChangePlayerEvent changePlayerEvent) throws GameException {
    GameLogic.isValidChangePlayer(gameState, changePlayerEvent);
    gameState.changeCurrentPlayer();
  }

  @Override
  public void makeMove(MakeMoveEvent makeMoveEvent) throws GameException {
    if (GameLogic.isValidMove(gameState, makeMoveEvent)) {
      gameState.makeMove(makeMoveEvent);
    }
  }
}
