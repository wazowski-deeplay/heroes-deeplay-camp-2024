package io.deeplay.camp.app.controller;

import io.deeplay.camp.app.service.GameJoinService;
import io.deeplay.camp.core.dto.server.GamePartiesDto;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import lombok.Setter;

@Setter
public class GameJoinController {
  /** Главный контроллер. */
  private MainController mainController;

  /** Сервис для подключеия. */
  private GameJoinService gameJoinService;

  /** Комбобокс с выбором комнат. */
  @FXML private ComboBox<String> roomComboBox;

  /** Инициализация. */
  @FXML
  private void initialize() {
    gameJoinService = new GameJoinService();
    gameJoinService.getParties();
  }

  /** Метод, обрабатывающий подключение к игре. */
  @FXML
  private void joinGame() {
    gameJoinService.joinGame(UUID.fromString(roomComboBox.getValue()));
  }

  /** Метод возврата обратно в меню. */
  @FXML
  private void goBack() {
    mainController.loadMenu();
  }

  /**
   * Метод установки списка пати в комбобокс из дто.
   *
   * @param gamePartiesDto дто с пати.
   */
  public void setParties(GamePartiesDto gamePartiesDto) {
    roomComboBox
        .getItems()
        .addAll(gamePartiesDto.getGamePartiesIds().stream().map(UUID::toString).toList());
  }
}
