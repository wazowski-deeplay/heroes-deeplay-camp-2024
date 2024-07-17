package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public class GameLogic {

  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement) {

    // У нас уже есть юнит и координаты и текущая доска

    // Текущая доска
    Board board = gameState.getCurrentBoard();

    // Координаты на доске
    int x = placement.getY();
    int y = placement.getX();

    // Юнит
    Unit unit = placement.getUnit();
    AttackType unitAttackType = placement.getUnit().getAttackType();

    // Результат
    boolean result = false;

    // Проверка есть ли место на доске
    if (board.isFullBoard()) {
      return result;
    }

    // Проверка занята ли клетка
    if (board.isTakenCell(x, y)) {
      return result;
    }

    // TO DO  Проверка с какой стороны стоит юнит
    // В нам понятно какой player ходит но не понятно клетка его или нет
    // Это дописать в board ?

    // Проверка соответсвует ли юнит тому куда он ставиться по правилам
    if (unitAttackType == AttackType.CLOSE_ATTACK) {
      if (placement.getY() == 1 || placement.getY() == 2) {
        return result = true;
      }
    } else if (placement.getY() == 0 || placement.getY() == 3) {
      return result = true;
    }




    return result;
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
