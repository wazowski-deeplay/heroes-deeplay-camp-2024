package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnitTest {

  @Test
  void getMaxHpArcher() {
    Archer unit = new Archer(PlayerType.FIRST_PLAYER);
    int expected = unit.getMaxHp();
    int actual = 10;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getMaxHpHealer() {
    Healer unit = new Healer(PlayerType.FIRST_PLAYER);
    int expected = unit.getMaxHp();
    int actual = 10;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getNowHpKnight() {
    Knight unit = new Knight(PlayerType.FIRST_PLAYER);
    int expected = unit.getNowHp();
    int actual = 15;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getDamageMage() {
    Mage unit = new Mage(PlayerType.FIRST_PLAYER);
    int expected = unit.getDamage();
    int actual = 2;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getAccuracyArcher() {
    Archer unit = new Archer(PlayerType.FIRST_PLAYER);
    int expected = unit.getAccuracy();
    int actual = 6;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void getArmorHealer() {
    Healer unit = new Healer(PlayerType.FIRST_PLAYER);
    int expected = unit.getArmor();
    int actual = 12;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void isAlive() {
    Healer unit = new Healer(PlayerType.FIRST_PLAYER);
    boolean expected = unit.isAlive();
    boolean actual = true;
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void isGeneral() {
    Healer unit = new Healer(PlayerType.FIRST_PLAYER);
    boolean expected = unit.isGeneral();
    boolean actual = false;
    Assertions.assertEquals(expected, actual);
  }
}
