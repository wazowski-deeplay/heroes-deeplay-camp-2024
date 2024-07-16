package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;

public class GameLogic {
  public static boolean isValidPlacement(GameState gameState, PlaceUnitEvent placement) {
    // можно также как и с isValidMove спрашивать юнита методом isCanPlace(position)

    return true;
  }

  public static boolean isValidChangePlayer(GameState gameState, ChangePlayerEvent changePlayer) {
    return gameState.getCurrentPlayer() == changePlayer.getRequester()&&gameState.getGameStage()!=GameStage.PLACEMENT_STAGE;
  }

  public static boolean isValidMove(GameState gameState, MakeMoveEvent move) {
    boolean result = false;
    Position from = move.getFrom();
    Position to = move.getTo();
    Unit attacker = move.getAttacker();

    boolean fullUnitInRow = (attacker.getPlayerType() == PlayerType.FIRST_PLAYER && gameState.getBoard().countUnitsRow(from.y()+1) > 1 && to.y() == from.y()+1) ||
            (attacker.getPlayerType() == PlayerType.SECOND_PLAYER && gameState.getBoard().countUnitsRow(from.y()-1) > 1 && to.y() == from.y()-1);

    boolean oneUnitInRow = (attacker.getPlayerType() == PlayerType.FIRST_PLAYER && gameState.getBoard().countUnitsRow(from.y()+1) == 1 && to.y() == from.y()+1 )||
            (attacker.getPlayerType() == PlayerType.SECOND_PLAYER && gameState.getBoard().countUnitsRow(from.y()-1) == 1  && to.y() == from.y()-1) ;

    boolean nullUnitInRow = (attacker.getPlayerType() == PlayerType.FIRST_PLAYER && gameState.getBoard().countUnitsRow(from.y()+1) == 0) ||
            (attacker.getPlayerType() == PlayerType.SECOND_PLAYER && gameState.getBoard().countUnitsRow(from.y()-1) == 0);

    if(gameState.getBoard().getUnit(to.x(),to.y()).getPlayerType() != attacker.getPlayerType()) {
      if (attacker.getUnitType() == UnitType.KNIGHT) {
        int radius = 1;
        if(oneUnitInRow  || nullUnitInRow){
          radius = 2;
        }
        if (Math.abs(from.y() - to.y()) <= radius && Math.abs(from.x() - to.x()) <= radius) {
          if(fullUnitInRow || oneUnitInRow || nullUnitInRow){
            result = true;
          }
        }
      }
      if (attacker.getUnitType() == UnitType.ARCHER) {
        result = true;
      }
      if (attacker.getUnitType() == UnitType.MAGE) {
        result = true;
      }
    }
    else{
      if(attacker.getUnitType() == UnitType.HEALER){
        result = true;
      }
    }
    return result;
  }
}
