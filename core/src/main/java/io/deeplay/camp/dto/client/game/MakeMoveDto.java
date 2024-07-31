package io.deeplay.camp.dto.client.game;

import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import java.util.UUID;

import io.deeplay.camp.entities.Unit;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MakeMoveDto extends ClientDto {
  private UUID gamePartyId;
  private int fromX;
  private int fromY;
  private int toX;
  private int toY;
  private Unit attacker;

  public MakeMoveDto(UUID gamePartyId, int fromX, int fromY, int toX, int toY, Unit attacker) {
    super(ClientDtoType.MAKE_MOVE);
    this.gamePartyId = gamePartyId;
    this.fromX = fromX;
    this.fromY = fromY;
    this.toX = toX;
    this.toY = toY;
    this.attacker = attacker;
  }
}
