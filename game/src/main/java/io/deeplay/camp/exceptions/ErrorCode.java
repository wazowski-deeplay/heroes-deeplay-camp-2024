package io.deeplay.camp.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
  PLAYER_CHANGE_IS_NOT_AVAILABLE("Передача хода недоступна!"),

  // Пример того, что может быть у Дениса
  MOVE_IS_NOT_AVAILABLE("Сейчас нельзя сделать ход!"),
  MOVE_IS_NOT_CORRECT("Сейчас нельзя сделать ход!");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
