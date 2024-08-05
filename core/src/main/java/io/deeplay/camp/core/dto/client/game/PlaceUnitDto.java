package io.deeplay.camp.core.dto.client.game;

import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import io.deeplay.camp.game.entities.UnitType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Getter
public class PlaceUnitDto extends ClientDto {
  private UUID gamePartyId;
  private int columns;
  private int rows;
  private UnitType unitType;
  private boolean inProcess;
  private boolean general;

  public PlaceUnitDto(
      UUID gamePartyId,
      int x,
      int y,
      @NonNull UnitType unitType,
      boolean inProcess,
      boolean general) {
    super(ClientDtoType.PLACE_UNIT);
    this.gamePartyId = gamePartyId;
    this.columns = x;
    this.rows = y;
    this.unitType = unitType;
    this.inProcess = inProcess;
    this.general = general;
  }
}
