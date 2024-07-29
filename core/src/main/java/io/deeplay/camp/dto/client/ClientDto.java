package io.deeplay.camp.dto.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "requestType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MakeMoveDto.class, name = "MAKE_MOVE"),
  @JsonSubTypes.Type(value = PlaceUnitDto.class, name = "PLACE_UNIT"),
  @JsonSubTypes.Type(value = ChangePlayerDto.class, name = "CHANGE_PLAYER"),
  @JsonSubTypes.Type(value = CreateGamePartyDto.class, name = "CREATE_PARTY"),
  @JsonSubTypes.Type(value = JoinGamePartyDto.class, name = "JOIN_PARTY"),
})
public abstract class ClientDto {
  private ClientDtoType clientDtoType;
  @Setter private UUID clientId;

  public ClientDto(ClientDtoType clientDtoType) {
    this.clientDtoType = clientDtoType;
  }
}
