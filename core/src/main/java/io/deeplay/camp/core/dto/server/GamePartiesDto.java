package io.deeplay.camp.core.dto.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GamePartiesDto extends ServerDto {
  private List<UUID> gamePartiesIds;

  public GamePartiesDto(List<UUID> gamePartiesIds) {
    super(ServerDtoType.GAME_PARTIES);
    this.gamePartiesIds = new ArrayList<>(gamePartiesIds);
  }
}
