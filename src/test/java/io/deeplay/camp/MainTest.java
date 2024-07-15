package io.deeplay.camp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {

  @Test
  void createInstanceTest() {
    Assertions.assertDoesNotThrow(Main::new);
  }
}
