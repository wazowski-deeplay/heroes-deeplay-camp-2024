module io.deeplay.camp.game {
  exports io.deeplay.camp.game.entities;
  exports io.deeplay.camp.game.exceptions;
  exports io.deeplay.camp.game.mechanics;
  exports io.deeplay.camp.game.events;
  exports io.deeplay.camp.game;

  requires java.base;
  requires static lombok;
  requires com.fasterxml.jackson.annotation;
  requires org.slf4j;
  requires java.desktop;
}
