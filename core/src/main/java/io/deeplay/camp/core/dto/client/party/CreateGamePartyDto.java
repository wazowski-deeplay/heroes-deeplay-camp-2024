package io.deeplay.camp.core.dto.client.party;

import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.ClientDtoType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateGamePartyDto extends ClientDto {
  private GameType gameType;

  public CreateGamePartyDto(GameType gameType) {
    super(ClientDtoType.CREATE_PARTY);
    this.gameType = gameType;
  }
}
