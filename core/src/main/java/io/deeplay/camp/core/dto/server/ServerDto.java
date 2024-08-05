package io.deeplay.camp.core.dto.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deeplay.camp.core.dto.client.game.OfferGiveUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "serverDtoType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = GamePartyInfoDto.class),
  @JsonSubTypes.Type(value = GameStateDto.class),
  @JsonSubTypes.Type(value = ErrorConnectionResponseDto.class),
  @JsonSubTypes.Type(value = ErrorGameResponseDto.class),
  @JsonSubTypes.Type(value = GamePartiesDto.class),
  @JsonSubTypes.Type(value = OfferGiveUpServerDto.class),
})
public abstract class ServerDto {
  ServerDtoType serverDtoType;

  public ServerDto(ServerDtoType serverDtoType) {
    this.serverDtoType = serverDtoType;
  }
}
