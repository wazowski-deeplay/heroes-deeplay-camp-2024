package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.exceptions.GameException;
import io.deeplay.camp.player.Player;
import io.deeplay.camp.player.Players;
import java.util.UUID;
import lombok.Getter;

/** Класс, отвечающий за конкретную игровую партию. Все запроы транслирует в Game. */
@Getter
public class GameParty {
  private final Game game;
  private final UUID gamePartyId;
  private final Players players;

  public GameParty(UUID gamePartyId) {
    players = new Players();
    this.gamePartyId = gamePartyId;
    game = new Game();
  }

  public void processPlaceUnit(PlaceUnitDto placeUnitDto) throws GameException {
    PlaceUnitEvent placeUnitEvent = DtoToEventConverter.convert(placeUnitDto);
    game.placeUnit(placeUnitEvent);
  }

  public void processMakeMove(MakeMoveDto makeMoveRequest) throws GameException {
    MakeMoveEvent makeMoveEvent = DtoToEventConverter.convert(makeMoveRequest);
    game.makeMove(makeMoveEvent);
    // надо уведомить двух игроков об измененном состоянии игры
    players.notifyPlayers(new String());
  }

  public void startGame() {}

  public void processChangePlayer(ChangePlayerDto changePlayerRequest) throws GameException {
    ChangePlayerEvent changePlayerEvent = DtoToEventConverter.convert(changePlayerRequest);
    game.changePlayer(changePlayerEvent);
  }

  public void addPlayer(Player player) {
    players.addPlayer(player.getPlayerType(), player);
  }

  public void close() {}
}
