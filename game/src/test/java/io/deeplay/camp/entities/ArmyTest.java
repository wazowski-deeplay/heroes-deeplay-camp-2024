package io.deeplay.camp.entities;

import io.deeplay.camp.mechanics.PlayerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArmyTest {

  Army army;

  @BeforeEach
  void setUp() {
    army = new Army(PlayerType.FIRST_PLAYER);
    army.getUnits()[0] = new Knight(army.getOwner());
    army.getUnits()[1] = new Knight(army.getOwner());
    army.getUnits()[2] = new Knight(army.getOwner());
    army.getUnits()[3] = new Mage(army.getOwner());
    army.getUnits()[4] = new Archer(army.getOwner());
    army.getUnits()[5] = new Healer(army.getOwner());
    army.getUnits()[1].setGeneral(true);
  }

  @Test
  void isAliveGeneralTestLive() {
    Assertions.assertTrue(army.isAliveGeneral());
  }

  @Test
  void isAliveGeneralTestBuff() {
    army.isAliveGeneral();
    Assertions.assertEquals(army.getUnits()[2].getArmor(), 18);
  }

  @Test
  void isAliveGeneralTestDebuff() {
    army.isAliveGeneral();
    army.getUnits()[1].setNowHp(-5);
    army.isAliveGeneral();
    Assertions.assertEquals(army.getUnits()[2].getArmor(), 15);
  }

  @Test
  void isAliveGeneralTestNoLive() {
    army.getUnits()[1].setGeneral(false);
    Assertions.assertNotEquals(true, army.isAliveGeneral());
  }
}
