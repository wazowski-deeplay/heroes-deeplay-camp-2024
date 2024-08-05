package io.deeplay.camp.core.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DrawServerDto extends ServerDto {
  private UUID gamePartyId;

  public DrawServerDto(UUID gamePartyId) {
    super(ServerDtoType.DRAW);
    this.gamePartyId = gamePartyId;
  }
}
