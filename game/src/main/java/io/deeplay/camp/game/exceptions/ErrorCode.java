package io.deeplay.camp.game.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // Смена хода
  PLAYER_CHANGE_IS_NOT_AVAILABLE("Передача хода недоступна!"),
  // Ход
  MOVE_IS_NOT_AVAILABLE("Сейчас нельзя сделать ход!"),
  MOVE_IS_NOT_CORRECT("Некорректный ход!"),
  GAME_IS_OVER("Игра окончена"),
  // Расстановка
  BOARD_IS_NOT_FULL("Не заполнена"),
  PLACEMENT_INCORRECT("Введены не корректные координаты"),
  GENERAL_IS_MISSING("Отсутствует генерал"),
  TOO_MANY_GENERAL("Слишком много генералов"),
  NOT_YOUR_TURN("Не твоя очередь"),
  UNDEFINED_ERROR("не известная ошибка");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
