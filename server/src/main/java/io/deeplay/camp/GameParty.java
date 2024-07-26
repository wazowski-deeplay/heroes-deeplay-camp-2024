package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.*;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.events.StartGameEvent;
import io.deeplay.camp.exceptions.GameException;
import lombok.Getter;

import java.util.UUID;

/** Класс, отвечающий за конкретную игровую партию. Все запроы транслирует в Game. */
@Getter
public class GameParty {
  private Game game;
  private UUID gamePartyId;

  // нужно добавить игроков

  public GameParty(UUID gamePartyId) {
    this.gamePartyId = gamePartyId;
    game = new Game();
  }

  public void processPlaceUnit(PlaceUnitRequest placeUnitRequest) throws GameException {
    PlaceUnitEvent placeUnitEvent = RequestToEventConverter.convert(placeUnitRequest);
    game.placeUnit(placeUnitEvent);
  }

  public void processMakeMove(MakeMoveRequest makeMoveRequest) throws GameException {
    MakeMoveEvent makeMoveEvent = RequestToEventConverter.convert(makeMoveRequest);
    game.makeMove(makeMoveEvent);
  }

  public void processStartGame(StartGameRequest startGameRequest) throws GameException {
    StartGameEvent startGameEvent = RequestToEventConverter.convert(startGameRequest);
    game.startGame(startGameEvent);
  }

  public void processChangePlayer(ChangePlayerRequest changePlayerRequest) throws GameException {
    ChangePlayerEvent changePlayerEvent = RequestToEventConverter.convert(changePlayerRequest);
    game.changePlayer(changePlayerEvent);
  }
}
