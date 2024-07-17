package io.deeplay.camp.events;

import io.deeplay.camp.mechanics.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePlayerEvent extends Event {
  PlayerType requester;
}
