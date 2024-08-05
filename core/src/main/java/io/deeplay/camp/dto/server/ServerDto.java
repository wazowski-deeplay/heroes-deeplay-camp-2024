package io.deeplay.camp.dto.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.dto.client.game.OfferGiveUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "serverDtoType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = GamePartyInfoDto.class, name = "GAME_PARTY_INFO"),
  @JsonSubTypes.Type(value = GameStateDto.class, name = "GAME_STATE"),
  @JsonSubTypes.Type(value = ErrorConnectionResponseDto.class, name = "ERROR_CONNECTION_INFO"),
  @JsonSubTypes.Type(value = ErrorGameResponseDto.class, name = "ERROR_GAME_INFO"),
  @JsonSubTypes.Type(value = OfferGiveUpServerDto.class, name = "OFFER_GIVE_UP"),
})
public abstract class ServerDto {
  ServerDtoType serverDtoType;

  public ServerDto(ServerDtoType serverDtoType) {
    this.serverDtoType = serverDtoType;
  }
}
