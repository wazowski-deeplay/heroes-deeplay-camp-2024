package io.deeplay.camp;

import io.deeplay.camp.entities.Board;
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

  @Override
  public void placeUnit(PlaceUnitEvent placeUnitEvent) {
    for (int i = 0; i < ((Board.ROWS * Board.COLUMNS) / 2); ) {
      if (GameLogic.isValidPlacement(gameState, placeUnitEvent)) {
        gameState.makePlacement(placeUnitEvent);
        gameState.getCurrentBoard();
        // Считаем только допустимые ходы
        i++;
      } else {
        gameState.getCurrentBoard();
      }
    }
    gameState.changeCurrentPlayer();
  }

  @Override
  public void changePlayer(ChangePlayerEvent changePlayerEvent) throws GameException {
    GameLogic.isValidChangePlayer(gameState, changePlayerEvent);
    gameState.changeCurrentPlayer();
  }

  @Override
  public void makeMove(MakeMoveEvent makeMoveEvent) {
    if (GameLogic.isValidMove(gameState, makeMoveEvent)) {
      gameState.makeMove(makeMoveEvent);
    }
  }
}
