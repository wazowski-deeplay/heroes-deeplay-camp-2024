package io.deeplay.camp.events;

import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlaceUnitEvent extends Event {
  private int x;
  private int y;
  private Unit unit;
}
