package io.deeplay.camp.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // Смена хода
  PLAYER_CHANGE_IS_NOT_AVAILABLE("Передача хода недоступна!"),
  // Ход
  MOVE_IS_NOT_AVAILABLE("Сейчас нельзя сделать ход!"),
  MOVE_IS_NOT_CORRECT("Некорректный ход!"),
  // Расстановка
  BOARD_IS_NOT_FULL("Не заполнена"),
  PLACEMENT_INCORRECT("Введены не корректные координаты"),
  GENERAL_IS_MISSING("Отсутствует генерал"),
  TO_MANY_GENERAL("Слишком много генералов");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
