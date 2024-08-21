package io.deeplay.camp.botfarm.bots;

import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import io.deeplay.camp.game.mechanics.PossibleActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RandomBot extends Bot{

    @Override
    public PlaceUnitEvent generatePlaceUnitEvent(GameState gameState) {
        List<PlaceUnitEvent> placeUnitEvents = gameState.getPossiblePlaces();
        if(!placeUnitEvents.isEmpty()){
            return placeUnitEvents.get((int)(Math.random()*placeUnitEvents.size()));
        }
        else{
            return null;
        }
    }

    @Override
    public MakeMoveEvent generateMakeMoveEvent(GameState gameState) {
        List<MakeMoveEvent> makeMoveEvents = gameState.getPossibleMoves();
        if(!makeMoveEvents.isEmpty()){
            return makeMoveEvents.get((int)(Math.random()*makeMoveEvents.size()));
        }
        else{
            return null;
        }
    }



}
