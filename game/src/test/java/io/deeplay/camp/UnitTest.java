package io.deeplay.camp;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.Healer;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Mage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnitTest {

  @Test
  void getMaxHpArcher() {
    Archer unit = new Archer();
    int expected = unit.getMaxHp();
    int actual = 10;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getMaxHpHealer() {
    Healer unit = new Healer();
    int expected = unit.getMaxHp();
    int actual = 10;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getNowHpKnight() {
    Knight unit = new Knight();
    int expected = unit.getNowHp();
    int actual = 10;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getDamageMage() {
    Mage unit = new Mage();
    int expected = unit.getDamage();
    int actual = 5;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getAccuracyArcher() {
    Archer unit = new Archer();
    int expected = unit.getAccuracy();
    int actual = 6;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getArmorHealer() {
    Healer unit = new Healer();
    int expected = unit.getArmor();
    int actual = 12;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void isAlive() {
    Healer unit = new Healer();
    boolean expected = unit.isAlive();
    boolean actual = true;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void isGeneral() {
    Healer unit = new Healer();
    boolean expected = unit.isGeneral();
    boolean actual = false;
    Assertions.assertEquals(expected, actual);
  }
}
