package io.deeplay.camp.server.exceptions;

import io.deeplay.camp.game.exceptions.GameException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GamePartyException extends Exception {
    private final GameException gameException;
    private final UUID gamePartyId;
    public GamePartyException(GameException e, UUID gamePartyId) {
        this.gameException = e;
        this.gamePartyId = gamePartyId;
    }
}
