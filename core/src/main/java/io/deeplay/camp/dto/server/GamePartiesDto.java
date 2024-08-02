package io.deeplay.camp.dto.server;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class GamePartiesDto extends ServerDto {
  private List<UUID> gamePartiesIds;

  public GamePartiesDto(List<UUID> gamePartiesIds) {
    super(ServerDtoType.GAME_PARTIES);
    this.gamePartiesIds = new ArrayList<>(gamePartiesIds);
  }
}
