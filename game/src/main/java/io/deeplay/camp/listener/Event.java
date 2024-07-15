package io.deeplay.camp.listener;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Event {
  private EventType eventType;
  private String message;
}
