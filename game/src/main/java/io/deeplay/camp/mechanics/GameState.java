package io.deeplay.camp.mechanics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.deeplay.camp.entities.Army;
import io.deeplay.camp.entities.AttackType;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameState {

  private Board board;

  private PlayerType currentPlayer;

  @JsonIgnore private Army armyFirst;
  @JsonIgnore private Army armySecond;
  private GameStage gameStage;

  public GameState() {
    board = new Board();
    armyFirst = new Army(PlayerType.FIRST_PLAYER);
    armySecond = new Army(PlayerType.SECOND_PLAYER);
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
    if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getAttackType()
        == AttackType.MASS_ATTACK) {
      if (board.getUnit(move.getFrom().x(), move.getFrom().y()).getPlayerType()
          == PlayerType.FIRST_PLAYER) {
        for (int i = 0; i < armySecond.getUnits().length; i++) {
          board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(armySecond.getUnits()[i]);
        }
      } else {
        for (int i = 0; i < armyFirst.getUnits().length; i++) {
          board.getUnit(move.getFrom().x(), move.getFrom().y()).playMove(armyFirst.getUnits()[i]);
        }
      }
    } else {
      board
          .getUnit(move.getFrom().x(), move.getFrom().y())
          .playMove(board.getUnit(move.getTo().x(), move.getTo().y()));
    }
  }

  public void makePlacement(PlaceUnitEvent placeUnit) {
    // Применяется поставновка фигуры к доске
    board.setUnit(placeUnit.getColums(), placeUnit.getRows(), placeUnit.getUnit());
  }

  public Board getCurrentBoard() {
    return board;
  }

  public PlayerType getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(PlayerType playerType) {
    this.currentPlayer = playerType;
  }
}
