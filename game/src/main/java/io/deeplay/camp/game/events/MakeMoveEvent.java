package io.deeplay.camp.game.events;

import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class MakeMoveEvent extends Event {
  private Position from;
  private Position to;
  private Unit attacker;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MakeMoveEvent that = (MakeMoveEvent) o;
    return Objects.equals(from, that.from) && Objects.equals(to, that.to)&&Objects.equals(attacker, that.attacker);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }
}
