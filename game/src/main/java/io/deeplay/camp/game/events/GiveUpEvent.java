package io.deeplay.camp.game.events;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GiveUpEvent extends Event {
  PlayerType playerType;
}
