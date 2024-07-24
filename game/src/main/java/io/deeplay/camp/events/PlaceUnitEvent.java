package io.deeplay.camp.events;

import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PlaceUnitEvent extends Event {

  private int colums;
  private int rows;
  private Unit unit;

  // Мы должны знать куда ставят, какого юнита, и кто ставит
  // playerType мы получаем из gameState, пример в тесте
  public PlaceUnitEvent(int x, int y, @NonNull Unit unit, PlayerType playerType) {
    this.colums = x;
    this.rows = y;
    this.unit = unit;
    unit.setPlayerType(playerType);
  }

  public PlaceUnitEvent(int x, int y, Unit unit) {
    this.colums = x;
    this.rows = y;
    this.unit = unit;
  }
}
