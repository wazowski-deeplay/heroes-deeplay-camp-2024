package io.deeplay.camp.dto.server;

import lombok.Getter;

@Getter
public enum ConnectionErrorCode {
  FULL_PARTY("Игровая сессия переполнена"),
  NON_EXISTENT_CONNECTION("Не существует такой сессии"),
  UNIDENTIFIED_ERROR("Не опознанная ошибка"),
  SERIALIZABLE_ERROR("Ошибка преобразования типов");
  private final String message;

  ConnectionErrorCode(String message) {
    this.message = message;
  }
}
