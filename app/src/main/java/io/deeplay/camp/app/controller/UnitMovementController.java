package io.deeplay.camp.app.controller;

import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import java.util.Stack;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

/** Контроллер для управления отображением и поведением юнитов в состоянии movement. */
public class UnitMovementController {
  /** Панель, содержащая элементы юнита. */
  @FXML Pane unitPane;

  /**
   * Возвращает панель юнита.
   *
   * @return панель юнита
   */
  public Pane getPane() {
    return unitPane;
  }

  /** Кнопка, представляющая юнита. */
  @Getter @FXML private Button unitButton;

  /** Метка для отображения здоровья юнита. */
  @FXML private Label healthLabel;

  /** Метка для отображения брони юнита. */
  @FXML private Label armorLabel;

  /** Метка для отображения урона юнита. */
  @FXML private Label damageLabel;

  /** Метка для отображения точности юнита. */
  @FXML private Label accuracyLabel;

  /** Игровой контроллер. */
  @Setter private GameController gameController;

  /** Координата X юнита. */
  @Setter private int x;

  /** Координата Y юнита. */
  @Setter private int y;

  /** Стек для хранения кликов пользователя. */
  @Setter private Stack<Position> clickStack;

  /** Флаг, указывающий, был ли юнит перемещен. */
  @Getter @Setter private boolean isMoved;

  /**
   * Возвращает текущее здоровье юнита.
   *
   * @return текущее здоровье юнита
   */
  int getHp() {
    return Integer.parseInt(healthLabel.getText());
  }

  /**
   * Устанавливает здоровье юнита.
   *
   * @param health здоровье юнита
   */
  public void setHealth(String health) {
    healthLabel.setText(health);
  }

  /**
   * Устанавливает броню юнита.
   *
   * @param armor броня юнита
   */
  public void setArmor(String armor) {
    armorLabel.setText(armor);
  }

  /**
   * Устанавливает урон юнита.
   *
   * @param damage урон юнита
   */
  public void setDamage(String damage) {
    damageLabel.setText(damage);
  }

  /**
   * Устанавливает точность юнита.
   *
   * @param accuracy точность юнита
   */
  public void setAccuracy(String accuracy) {
    accuracyLabel.setText(accuracy);
  }

  /**
   * Устанавливает юнита и обновляет его характеристики.
   *
   * @param unit юнит
   */
  public void setUnit(Unit unit) {
    setUnitButtonText(unit.getUnitType());
    setHealth(String.valueOf(unit.getCurrentHp()));
    setArmor(String.valueOf(unit.getArmor()));
    setDamage(String.valueOf(unit.getDamage()));
    setAccuracy(String.valueOf(unit.getAccuracy()));
  }

  /**
   * Устанавливает текст кнопки юнита в зависимости от его типа.
   *
   * @param unitType тип юнита
   */
  public void setUnitButtonText(UnitType unitType) {
    switch (unitType) {
      case KNIGHT:
        Platform.runLater(() -> unitButton.setText("Knight"));
        break;
      case MAGE:
        Platform.runLater(() -> unitButton.setText("Wizard"));
        break;
      case HEALER:
        Platform.runLater(() -> unitButton.setText("Healer"));
        break;
      case ARCHER:
        Platform.runLater(() -> unitButton.setText("Archer"));
        break;
      default:
        System.out.println("Unknown unit type");
        break;
    }
  }

  /** Обрабатывает клик по юниту. */
  public void handleUnitClick() {
    clickStack.push(new Position(x, y));
    gameController.updateBoardAfterClick();
  }
}
