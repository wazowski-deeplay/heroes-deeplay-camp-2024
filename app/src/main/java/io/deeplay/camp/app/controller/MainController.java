package io.deeplay.camp.app.controller;

import io.deeplay.camp.app.Client;
import io.deeplay.camp.app.MainApp;
import io.deeplay.camp.app.model.GameModel;
import io.deeplay.camp.app.model.GameModelManager;
import io.deeplay.camp.core.dto.server.GamePartyInfoDto;
import io.deeplay.camp.game.mechanics.GameStage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Главный контроллер, отвечающий за переход между окнами. */
public class MainController {
  private static final Logger logger = LoggerFactory.getLogger(MainController.class);
  /** Главная панель */
  @FXML public BorderPane mainPane;

  /** Инициализация котроллера вместе с клинетом. */
  @FXML
  private void initialize() {
    Client.getInstance().setMainController(this);
    loadMenu();
  }

  /** Метод загрузки меню. */
  @FXML
  public void loadMenu() {
    try {
      FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/menu-view.fxml"));
      Pane menu = loader.load();
      MenuController menuController = loader.getController();
      menuController.setMainController(this);
      mainPane.setCenter(menu);
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }

  /** Метод загрузки окна с конфигом игры. */
  @FXML
  public void loadGameConfig() {
    try {
      FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/game-config-view.fxml"));
      Pane gameConfig = loader.load();
      GameConfigController gameConfigController = loader.getController();
      gameConfigController.setMainController(this);
      mainPane.setCenter(gameConfig);
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }

  /** Метод загрузки окна с настройками игры. */
  @FXML
  public void loadSettings() {
    try {
      FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/settings-view.fxml"));
      Pane settings = loader.load();
      SettingsController settingsController = loader.getController();
      settingsController.setMainController(this);
      mainPane.setCenter(settings);
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }

  /** Метод загрузки окна с окном присоединения к игре. */
  @FXML
  public void loadGameJoin() {
    try {
      FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/game-join-view.fxml"));
      Pane gameJoin = loader.load();
      GameJoinController gameJoinController = loader.getController();
      Client.getInstance().setGameJoinController(gameJoinController);
      gameJoinController.setMainController(this);
      mainPane.setCenter(gameJoin);
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }

  /**
   * Метод открытия окна игры по дто.
   *
   * @param gamePartyInfoDto Информация о пати.
   */
  public void openGame(GamePartyInfoDto gamePartyInfoDto) {
    FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/game-view.fxml"));
    try {
      Pane gamePane = loader.load();
      GameController gameController = loader.getController();
      GameModel gameModel =
          GameModel.builder()
              .gameController(gameController)
              .gamePartyId(gamePartyInfoDto.getGamePartyId())
              .thisPlayer(gamePartyInfoDto.getPlayerType())
              .gameStage(GameStage.PLACEMENT_STAGE)
              .build();
      gameController.setGameModel(gameModel);
      GameModelManager.getInstance().addParty(gamePartyInfoDto.getGamePartyId(), gameModel);
      gameController.loadUnitPlacementView();
      Platform.runLater(() -> gameController.setWaiting(true));
      Stage gameStage = new Stage();
      gameStage.setTitle("Game Window");
      gameStage.setScene(new Scene(gamePane));
      gameStage.show();
    } catch (IOException e) {
      logger.error("Ошибка с доступом к ресурсам!");
    }
  }
}
