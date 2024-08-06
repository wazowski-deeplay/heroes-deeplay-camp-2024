package io.deeplay.camp.game.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrawEvent extends Event {
    private List<Boolean> draw = new ArrayList<>();
}
