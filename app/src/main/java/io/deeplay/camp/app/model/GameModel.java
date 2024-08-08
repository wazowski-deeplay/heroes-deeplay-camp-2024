package io.deeplay.camp.app.model;

import io.deeplay.camp.app.controller.GameController;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import java.util.UUID;
import javafx.application.Platform;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Модель игры для отображения. */
@Builder
@Getter
public class GameModel {
  /** Id патию */
  private UUID gamePartyId;

  /** Очередность хода для играющего игрока. */
  private PlayerType thisPlayer;

  /** Очередность хода для врага. */
  @Setter private PlayerType currentPlayer;

  /** Этап игры. */
  @Setter private GameStage gameStage;

  /** Игровой контроллер */
  private GameController gameController;

  /**
   * Метод обновления игрового состояния через контроллер.
   *
   * @param gameState Игровое состояние.
   */
  public void updateGameState(GameState gameState) {
    Platform.runLater(() -> gameController.updateGamePane(gameState));
  }

  /**
   * По модели определяет сейчас ход играющего игрока или нет.
   *
   * @return Ходит/не ходит.
   */
  public boolean isThisPlayerTurn() {
    return thisPlayer == currentPlayer;
  }
}
