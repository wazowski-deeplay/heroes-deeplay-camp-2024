package io.deeplay.camp.events;

import io.deeplay.camp.mechanics.GameStage;
import io.deeplay.camp.mechanics.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GiveUpEvent extends Event{
    PlayerType playerType;
}
