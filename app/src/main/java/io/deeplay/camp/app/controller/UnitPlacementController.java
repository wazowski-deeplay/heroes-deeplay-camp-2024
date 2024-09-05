package io.deeplay.camp.app.controller;

import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

public class UnitPlacementController {
  /** Чекбокс для выбора генерала. */
  @FXML private CheckBox generalCheckbox;

  /** Комбобокс для выбора юнита. */
  @FXML private ComboBox<String> unitComboBox;

  /** Лейбл здоровья. */
  @FXML private Label healthLabel;

  /** Лебл брони. */
  @FXML private Label armorLabel;

  /** Лейбл урона. */
  @FXML private Label damageLabel;

  /** Лейбл точности. */
  @FXML private Label accuracyLabel;

  /** Игровой контроллер. */
  @Setter private GameController gameController;

  /** Флаг выбора генерала. */
  @Getter private boolean generalSelected;

  /** Метод инициализации. */
  @FXML
  public void initialize() {
    generalSelected = false;
    generalCheckbox
        .selectedProperty()
        .addListener(
            (obs, wasSelected, isNowSelected) -> {
              if (isNowSelected) {
                generalSelected = true;
                gameController.updateGeneralCheckboxes(this);
              } else {
                generalSelected = false;
                gameController.updateGeneralCheckboxes(this);
              }
            });

    unitComboBox
        .valueProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              gameController.updatePassTurnButtonState();
            });
  }

  /**
   * Метод блокировки чекбокса генерала.
   *
   * @param disable Блокировать/разблокировать.
   */
  public void disableGeneralCheckbox(boolean disable) {
    generalCheckbox.setDisable(disable);
  }

  public boolean isComboboxSelected() {
    return unitComboBox.getValue() != null && !unitComboBox.getValue().isEmpty();
  }

  public void setHealth(String health) {
    Platform.runLater(() -> healthLabel.setText(health));
  }

  public void setArmor(String armor) {
    Platform.runLater(() -> armorLabel.setText(armor));
  }

  public void setDamage(String damage) {
    Platform.runLater(() -> damageLabel.setText(damage));
  }

  public void setAccuracy(String accuracy) {
    Platform.runLater(() -> accuracyLabel.setText(accuracy));
  }

  /**
   * Устанавливает юнита на панель.
   *
   * @param unit Юнит.
   */
  public void setUnit(Unit unit) {
    setUnitComboBoxValue(unit.getUnitType());
    setHealth(String.valueOf(unit.getCurrentHp()));
    setArmor(String.valueOf(unit.getArmor()));
    setDamage(String.valueOf(unit.getDamage()));
    setAccuracy(String.valueOf(unit.getAccuracy()));
  }

  /**
   * Возвращает UnitType по выбранному элементу в комбобоксе.
   *
   * @return UnitType.
   */
  public UnitType getUnitType() {
    return switch (unitComboBox.getValue()) {
      case "Knight" -> UnitType.KNIGHT;
      case "Wizard" -> UnitType.MAGE;
      case "Healer" -> UnitType.HEALER;
      case "Archer" -> UnitType.ARCHER;
      default -> null;
    };
  }

  /**
   * Устанавливает значение в комбобокс по UnitTYpe.
   *
   * @param unitType UnitType.
   */
  public void setUnitComboBoxValue(UnitType unitType) {
    switch (unitType) {
      case KNIGHT:
        Platform.runLater(() -> unitComboBox.setValue("Knight"));
        break;
      case MAGE:
        Platform.runLater(() -> unitComboBox.setValue("Wizard"));
        break;
      case HEALER:
        Platform.runLater(() -> unitComboBox.setValue("Healer"));
        break;
      case ARCHER:
        Platform.runLater(() -> unitComboBox.setValue("Archer"));
        break;
      default:
        break;
    }
  }
}
