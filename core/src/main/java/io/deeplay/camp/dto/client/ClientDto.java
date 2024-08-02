package io.deeplay.camp.dto.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.GiveUpDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "clientDtoType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MakeMoveDto.class),
  @JsonSubTypes.Type(value = PlaceUnitDto.class),
  @JsonSubTypes.Type(value = ChangePlayerDto.class),
  @JsonSubTypes.Type(value = CreateGamePartyDto.class),
  @JsonSubTypes.Type(value = JoinGamePartyDto.class),
  @JsonSubTypes.Type(value = GiveUpDto.class),
})
public abstract class ClientDto {
  private ClientDtoType clientDtoType;
  @Setter private UUID clientId;

  public ClientDto(ClientDtoType clientDtoType) {
    this.clientDtoType = clientDtoType;
  }
}
