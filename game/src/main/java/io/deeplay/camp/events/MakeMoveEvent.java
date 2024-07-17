package io.deeplay.camp.events;

import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MakeMoveEvent extends Event {
  private Position from;
  private Position to;
  private Unit attacker;
}
