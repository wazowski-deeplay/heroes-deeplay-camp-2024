package io.deeplay.camp.server;

import io.deeplay.camp.core.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.core.dto.client.game.MakeMoveDto;
import io.deeplay.camp.core.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.mechanics.PlayerType;

public class DtoToEventConverter {

  public static PlaceUnitEvent convert(PlaceUnitDto placeUnitDto, PlayerType playerType) {
    Unit unit = null;
    switch (placeUnitDto.getUnitType()) {
      case KNIGHT -> unit = new Knight(playerType);
      case ARCHER -> unit = new Archer(playerType);
      case HEALER -> unit = new Healer(playerType);
      case MAGE -> unit = new Mage(playerType);
      default -> unit = null;
    }
    return new PlaceUnitEvent(
        placeUnitDto.getColumns(),
        placeUnitDto.getRows(),
        unit,
        playerType,
        placeUnitDto.isInProcess(),
        placeUnitDto.isGeneral());
  }

  public static MakeMoveEvent convert(MakeMoveDto makeMoveDto, Board board) {
    return new MakeMoveEvent(
        new Position(makeMoveDto.getFromX(), makeMoveDto.getFromY()),
        new Position(makeMoveDto.getToX(), makeMoveDto.getToY()),
        board.getUnit(makeMoveDto.getFromX(), makeMoveDto.getFromY()));
  }

  public static ChangePlayerEvent convert(ChangePlayerDto changePlayerDto, PlayerType playerType) {
    return new ChangePlayerEvent(playerType);
  }

  public static GiveUpEvent convert(PlayerType playerType) {
    return new GiveUpEvent(playerType);
  }
}
