module io.deeplay.camp.game {
  exports io.deeplay.camp.game;
  exports io.deeplay.camp.game.entities;
  exports io.deeplay.camp.game.exceptions;
  exports io.deeplay.camp.game.mechanics;
  exports io.deeplay.camp.game.events;

  requires java.base;
  requires static lombok;
  requires com.fasterxml.jackson.annotation;
  requires org.slf4j;
  requires java.desktop;

  opens io.deeplay.camp.game.entities to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.game.exceptions to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.game.mechanics to
      com.fasterxml.jackson.databind;
  opens io.deeplay.camp.game.events to
      com.fasterxml.jackson.databind;
}
