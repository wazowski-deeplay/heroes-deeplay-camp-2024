package io.deeplay.camp.game.events;

import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MakeMoveEvent extends Event {
  private Position from;
  private Position to;
  private Unit attacker;
}
