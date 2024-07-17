package io.deeplay.camp;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;

public interface GameListener {
  void startGame(StartGameEvent event);

  void placeUnit(PlaceUnitEvent event);

  void changePlayer(ChangePlayerEvent event);

  void makeMove(MakeMoveEvent event);
}
