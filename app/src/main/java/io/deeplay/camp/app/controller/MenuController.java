package io.deeplay.camp.app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import lombok.Setter;

@Setter
public class MenuController {

  /** Главный контроллер. */
  private MainController mainController;

  /** Метод, открывающий окно подключения. */
  @FXML
  public void showJoinGameView() {
    mainController.loadGameJoin();
  }

  /** Метод, открывающий окно создания игры. */
  @FXML
  public void showCreateGame() {
    mainController.loadGameConfig();
  }

  /** Метод открывающий окно настроек. */
  @FXML
  public void showSettingsView() {
    mainController.loadSettings();
  }

  /** Метод выхода. */
  @FXML
  public void exitGame() {
    Platform.exit();
  }
}
