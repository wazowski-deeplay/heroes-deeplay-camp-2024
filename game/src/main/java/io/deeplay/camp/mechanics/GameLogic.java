package io.deeplay.camp.mechanics;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public class GameLogic {
  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement) {
    // можно также как и с isValidMove спрашивать юнита методом isCanPlace(position)

    return true;
  }

  public static boolean isValidChangePlayer(GameState gameState, ChangePlayerEvent changePlayer) {
    return true;
  }

  public static boolean isValidMove(GameState gameState, MakeMoveEvent move) {
    // достаём юнита и только спрашиваем у него, передавая ход и координаты,
    // чтобы он проверил, может ли походить. Если может, то это сделает уже класс game манипулируя
    // gameState
    return true;
  }
}
