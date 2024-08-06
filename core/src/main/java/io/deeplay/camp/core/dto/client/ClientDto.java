package io.deeplay.camp.core.dto.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.core.dto.client.game.*;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.core.dto.client.party.GetPartiesDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import java.util.UUID;

import io.deeplay.camp.core.dto.server.RestartServerDto;
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
  @JsonSubTypes.Type(value = GetPartiesDto.class),
  @JsonSubTypes.Type(value = SwitchPartyDto.class),
  @JsonSubTypes.Type(value = GiveUpDto.class),
  @JsonSubTypes.Type(value = OfferDrawDto.class),
  @JsonSubTypes.Type(value = DrawDto.class),
  @JsonSubTypes.Type(value = OfferRestartDto.class),
  @JsonSubTypes.Type(value = RestartDto.class),
})
public abstract class ClientDto {
  private ClientDtoType clientDtoType;
  @Setter private UUID clientId;

  public ClientDto(ClientDtoType clientDtoType) {
    this.clientDtoType = clientDtoType;
  }
}
