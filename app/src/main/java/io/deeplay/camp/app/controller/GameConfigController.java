package io.deeplay.camp.app.controller;

import io.deeplay.camp.app.service.GameConfigService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.Setter;

@Setter
public class GameConfigController {

  /** Главный контроллер */
  private MainController mainController;

  /** Сервис для конфигурации игры. */
  private GameConfigService gameConfigService;

  /** Радиобаттон для выбора режима против человека. */
  @FXML private RadioButton humanModeRadio;

  /** Радиобаттон для выбора режима против бота. */
  @FXML private RadioButton botModeRadio;

  /** Группа радиобаттонов. */
  @FXML private ToggleGroup gameModeGroup;

  /** Комбобокс с ботами. */
  @FXML private ComboBox<String> botComboBox;

  /** Hbox для лейбла и комбобокса с ботами. */
  @FXML private HBox botHBox;

  /** Инициализация. */
  @FXML
  private void initialize() {
    gameConfigService = new GameConfigService();
    botComboBox.getItems().addAll("Бот 1", "Бот 2", "Бот 3");
    gameModeGroup = new ToggleGroup();
    humanModeRadio.setToggleGroup(gameModeGroup);
    botModeRadio.setToggleGroup(gameModeGroup);
    humanModeRadio.setSelected(true);
    modeSelected();
  }

  /** Меняет отображение в зависимости от выбранного режима. */
  @FXML
  private void modeSelected() {
    if (humanModeRadio.isSelected()) {
      botHBox.setVisible(false);
      botHBox.setDisable(true);
    } else if (botModeRadio.isSelected()) {
      botHBox.setVisible(true);
      botHBox.setDisable(false);
    }
  }

  /** Метод создания игры. */
  @FXML
  private void createGame() {
    if (botModeRadio.isSelected()) {
      gameConfigService.createGameVsBot();
      goBack();
    }
    if (humanModeRadio.isSelected()) {
      gameConfigService.createGameVsHuman();
      goBack();
    }
  }

  /** Метод выхода назад. */
  @FXML
  private void goBack() {
    mainController.loadMenu();
  }
}
