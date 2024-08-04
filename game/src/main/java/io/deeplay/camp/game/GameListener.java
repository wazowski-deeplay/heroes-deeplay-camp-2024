package io.deeplay.camp.game;

import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.events.StartGameEvent;
import io.deeplay.camp.game.exceptions.GameException;

public interface GameListener {
  void startGame(StartGameEvent event);

  void placeUnit(PlaceUnitEvent event) throws GameException;

  void changePlayer(ChangePlayerEvent event) throws GameException;

  void makeMove(MakeMoveEvent event) throws GameException;
}
