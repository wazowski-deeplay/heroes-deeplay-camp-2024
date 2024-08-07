package io.deeplay.camp.core.dto.server;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExitPartyServerDto extends ServerDto {
  private UUID gamePartyId;

  public ExitPartyServerDto(UUID gamePartyId) {
    super(ServerDtoType.EXIT_PARTY);
    this.gamePartyId = gamePartyId;
  }
}
