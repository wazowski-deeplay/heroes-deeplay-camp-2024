package io.deeplay.camp.game.events;

import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePlayerEvent extends Event {
  PlayerType requester;
}
