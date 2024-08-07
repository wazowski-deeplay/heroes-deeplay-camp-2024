package io.deeplay.camp.game.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Defender {
  private Unit defenderUnit;
  private boolean wasHit = false;

  public Defender(Unit defenderUnit, boolean wasHit) {
    this.defenderUnit = defenderUnit;
    this.wasHit = wasHit;
  }
}
