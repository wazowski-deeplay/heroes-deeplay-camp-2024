package io.deeplay.camp.listener;

public interface GameListener {
  void startGame(Event event);

  void placeUnit(Event event);

  void changePlayer(Event event);

  void chooseGeneral(Event event);

  void makeMove(Event event);

  void endGame(Event event);
}
