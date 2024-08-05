package io.deeplay.camp.game.events;

import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PlaceUnitEvent extends Event {

  private int columns;
  private int rows;
  private Unit unit;
  private boolean inProcess = true;
  private boolean general = false;

  // Мы должны знать куда ставят, какого юнита, и кто ставит
  // playerType мы получаем из gameState, пример в тесте
  public PlaceUnitEvent(
      int x, int y, @NonNull Unit unit, PlayerType playerType, boolean inProcess, boolean general) {
    this.columns = x;
    this.rows = y;
    this.unit = unit;
    this.general = general;
    unit.setPlayerType(playerType);
    unit.setGeneral(general);
    this.inProcess = inProcess;
  }
}
