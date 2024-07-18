package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public class GameLogic {

  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement) {

    Board board = gameState.getCurrentBoard();
    Unit unit = placement.getUnit();
    AttackType unitAttackType = placement.getUnit().getAttackType();
    int x = placement.getX();
    int y = placement.getY();
    boolean result = false;

    // Проверка есть ли место на доске
    if (board.isFullBoard()) {
      return result;
    }

    // Проверка занята ли клетка
    if (board.isTakenCell(x, y)) {
      return result;
    }

    // Проверка на сторону юнита
    if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
      if (y < (Board.ROWS/2)) {
        return result = true;
      } else {
        return result;
      }
    }
    else {
      if (y > ((Board.ROWS/2)-1) && y < Board.ROWS){
        return result = true;
      }
      else
        return result;
    }

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
