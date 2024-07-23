package io.deeplay.camp.mechanics;

import io.deeplay.camp.events.ChangePlayerEvent;
import io.deeplay.camp.exceptions.ErrorCode;
import io.deeplay.camp.exceptions.GameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogic {
  private static final Logger logger = LoggerFactory.getLogger(GameLogic.class);

  /**
   * Метод проверяет событие перехода хода другому игроку.
   *
   * @param gameState Актуальное игровое состояние.
   * @param changePlayerEvent Событие передачи хода.
   * @throws GameException Если расстановка не завершена, либо переход запросил не тот игрок.
   */
  public static void isValidChangePlayer(GameState gameState, ChangePlayerEvent changePlayerEvent)
      throws GameException {
    if (gameState.getCurrentPlayer() == changePlayerEvent.getRequester()
        && gameState.getGameStage() != GameStage.PLACEMENT_STAGE) {
      logger.atInfo().log("{} has completed his turn", changePlayerEvent.getRequester().name());
    } else {
      logger.atInfo().log(
          "{} passes the move out of his turn", changePlayerEvent.getRequester().name());
      throw new GameException(ErrorCode.PLAYER_CHANGE_IS_NOT_AVAILABLE);
    }
  }

}
