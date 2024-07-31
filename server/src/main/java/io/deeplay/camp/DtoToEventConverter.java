package io.deeplay.camp;

import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.entities.*;
import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.events.MakeMoveEvent;
import io.deeplay.camp.events.PlaceUnitEvent;
import io.deeplay.camp.mechanics.PlayerType;

public class DtoToEventConverter {

  public static PlaceUnitEvent convert(PlaceUnitDto placeUnitDto, PlayerType playerType) {
    Unit unit = null;
    switch (placeUnitDto.getUnitType()) {
      case KNIGHT -> unit = new Knight(playerType);
      case ARCHER -> unit = new Archer(playerType);
      case HEALER -> unit = new Healer(playerType);
      case MAGE -> unit = new Mage(playerType);
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
}
