package io.deeplay.camp.dto.client;

import ch.qos.logback.core.net.server.Client;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.dto.client.game.ChangePlayerRequest;
import io.deeplay.camp.dto.client.game.MakeMoveRequest;
import io.deeplay.camp.dto.client.game.PlaceUnitRequest;
import io.deeplay.camp.dto.client.game.StartGameRequest;
import io.deeplay.camp.dto.client.party.CreatePartyRequest;
import io.deeplay.camp.dto.client.party.JoinPartyRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "requestType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MakeMoveRequest.class, name = "MAKE_MOVE"),
  @JsonSubTypes.Type(value = PlaceUnitRequest.class, name = "PLACE_UNIT"),
  @JsonSubTypes.Type(value = ChangePlayerRequest.class, name = "CHANGE_PLAYER"),
  @JsonSubTypes.Type(value = StartGameRequest.class, name = "START_GAME"),
  @JsonSubTypes.Type(value = CreatePartyRequest.class, name = "CREATE_PARTY"),
  @JsonSubTypes.Type(value = JoinPartyRequest.class, name = "JOIN_PARTY"),
})
public abstract class Request {
  private RequestType requestType;
  @Setter
  private UUID clientId;
  public Request(RequestType requestType) {
    this.requestType = requestType;
  }
}
