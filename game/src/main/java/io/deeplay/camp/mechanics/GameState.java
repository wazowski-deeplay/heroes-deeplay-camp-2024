package io.deeplay.camp.mechanics;

import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.entities.UnitType;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameState {

  private Board board;

  private PlayerType currentPlayer;

  private GameStage gameStage;

  public GameState() {
    board = new Board();
    currentPlayer = PlayerType.FIRST_PLAYER;
    gameStage = GameStage.PLACEMENT_STAGE;
  }

  public void changeCurrentPlayer() {
    if (currentPlayer == PlayerType.FIRST_PLAYER) {
      currentPlayer = PlayerType.SECOND_PLAYER;
    } else {
      currentPlayer = PlayerType.FIRST_PLAYER;
    }
  }

  // методы чисто для применения, проверка происходит до их использования
  public void makeMove(MakeMoveEvent move) {
      //Проверка на то, не является ли атакующий Магом
      if(move.getAttacker().getUnitType() != UnitType.MAGE){
        board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(board.getUnit(move.getTo().x(), move.getTo().y()));
      }//Если является, то перебираем всех вражеских юнитов и наносим им удар
      else{
        for (int i = 0; i < board.getUnits().length; i++) {
          for (int j = 0; j < board.getUnits()[i].length; j++) {
            if(board.getUnits()[i][j].getPlayerType() != move.getAttacker().getPlayerType()){
              board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(board.getUnit(i,j));
            }
          }
        }
      }
  }

  public void makePlacement(PlaceUnitEvent placeUnit) {}

  public Board getCurrentBoard() {
    return board;
  }
}
