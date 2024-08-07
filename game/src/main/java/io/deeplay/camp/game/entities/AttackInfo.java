package io.deeplay.camp.game.entities;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AttackInfo {
  private Unit attacker;
  private List<Defender> defenders;

  public AttackInfo(Unit attacker, List<Defender> defenders) {
    this.attacker = attacker;
    this.defenders = defenders;
  }
}
