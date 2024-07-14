package io.deeplay.camp.camp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {

  @Test
  void createInstanceTest() {
    Assertions.assertDoesNotThrow(Main::new);
  }
}
