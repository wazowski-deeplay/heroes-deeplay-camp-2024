package io.deeplay.camp;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.exceptions.GameException;

public interface GameListener {
  void startGame(StartGameEvent event);

  void placeUnit(PlaceUnitEvent event) throws GameException;

  void changePlayer(ChangePlayerEvent event) throws GameException;

  void makeMove(MakeMoveEvent event) throws GameException;
}
