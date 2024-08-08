package io.deeplay.camp.app.model;

import io.deeplay.camp.core.dto.server.GameStateDto;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Менеджер игровых партий у клиента. */
public class GameModelManager {
  /** Состояние менеджера(для singleton) */
  private static volatile GameModelManager instance;

  /** Игровые партии на клиенте. */
  private final Map<UUID, GameModel> parties;

  /** Конструктор. */
  public GameModelManager() {
    this.parties = new HashMap<>();
  }

  /**
   * Геттер для реализации синглтона.
   *
   * @return Состояние менеджера.
   */
  public static GameModelManager getInstance() {
    if (instance == null) {
      synchronized (GameModelManager.class) {
        if (instance == null) {
          instance = new GameModelManager();
        }
      }
    }
    return instance;
  }

  /**
   * Метод добавления игровых партий.
   *
   * @param uuid id игры.
   * @param gameModel модель игры.
   */
  public void addParty(UUID uuid, GameModel gameModel) {
    this.parties.put(uuid, gameModel);
  }

  /**
   * Метод обновления игрового состояния в контроллере.
   *
   * @param gameStateDto Игровое состояние.
   */
  public void updateGame(GameStateDto gameStateDto) {
    GameModel gameModel = parties.get(gameStateDto.getGamePartyId());
    gameModel.updateGameState(gameStateDto.getGameState());
  }
}
