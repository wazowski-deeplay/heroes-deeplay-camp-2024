package io.deeplay.camp.dto.client.party;

import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.ClientDtoType;
import lombok.Getter;

@Getter
public class CreateGamePartyDto extends ClientDto {
  private GameType gameType;

  public CreateGamePartyDto(GameType gameType) {
    super(ClientDtoType.CREATE_PARTY);
    this.gameType = gameType;
  }
}
