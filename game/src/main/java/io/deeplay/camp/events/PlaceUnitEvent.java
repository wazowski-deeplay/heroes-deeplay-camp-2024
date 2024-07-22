package io.deeplay.camp.events;

import io.deeplay.camp.entities.Unit;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PlaceUnitEvent extends Event {

  private int columns;
  private int rows;
  private Unit unit;
  private boolean inProcess;

  // Мы должны знать куда ставят, какого юнита, и кто ставит
  // playerType мы получаем из gameState, пример в тесте
  public PlaceUnitEvent(
      int x, int y, @NonNull Unit unit, PlayerType playerType, boolean inProcess, boolean general) {
    this.columns = x;
    this.rows = y;
    this.unit = unit;
    unit.setPlayerType(playerType);
    unit.setGeneral(general);
    this.inProcess = inProcess;
  }
}
