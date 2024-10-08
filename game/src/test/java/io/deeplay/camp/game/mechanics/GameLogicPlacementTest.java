package io.deeplay.camp.game.mechanics;

import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.ErrorCode;
import io.deeplay.camp.game.exceptions.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameLogicPlacementTest {

  private GameState gameState;

  @BeforeEach
  void setUp() {
    gameState = new GameState();
  }

  @Test
  void testCorrectedCoordinatesX_IsNotValide() {
    Unit knight = new Knight(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(5, 0, knight, gameState.getCurrentPlayer(), true, false);
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(event));
    assertEquals(ErrorCode.PLACEMENT_INCORRECT, gameException.getErrorCode());
  }

  @Test
  void testCorrectedCoordinatesY_IsNotValide() {
    Unit knight = new Knight(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(0, 5, knight, gameState.getCurrentPlayer(), true, false);
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(event));
    assertEquals(ErrorCode.PLACEMENT_INCORRECT, gameException.getErrorCode());
  }

  @Test
  void testChangeUnit_IsValide() {
    Unit knight = new Knight(gameState.getCurrentPlayer());
    Unit mage = new Mage(gameState.getCurrentPlayer());

    PlaceUnitEvent event1 =
        new PlaceUnitEvent(1, 1, knight, gameState.getCurrentPlayer(), true, false);
    assertDoesNotThrow(() -> gameState.makePlacement(event1));

    gameState.getCurrentBoard().setUnit(1, 1, knight);

    Unit first = gameState.getCurrentBoard().getUnit(1, 1);

    PlaceUnitEvent event2 =
        new PlaceUnitEvent(1, 1, mage, gameState.getCurrentPlayer(), true, false);
    assertDoesNotThrow(() -> gameState.makePlacement(event2));
    gameState.getCurrentBoard().setUnit(1, 1, mage);

    Unit second = gameState.getCurrentBoard().getUnit(1, 1);
    assertNotEquals(first, second);
  }

  @Test
  void testCorrectSideFirstPlayer_IsNotValid() {
    Unit knight = new Knight(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(2, 2, knight, gameState.getCurrentPlayer(), true, false);
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(event));
    assertEquals(ErrorCode.PLACEMENT_INCORRECT, gameException.getErrorCode());
  }

  @Test
  void testCorrectSideFirstPlayer_IsValid() {
    Unit reng = new Archer(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(1, 1, reng, gameState.getCurrentPlayer(), true, false);
    assertDoesNotThrow(() -> gameState.makePlacement(event));
  }

  @Test
  void testCorrectSideSecondPlayer_IsNotValid() {
    gameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
    Unit reng = new Knight(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(1, 1, reng, gameState.getCurrentPlayer(), true, false);
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(event));
    assertEquals(ErrorCode.PLACEMENT_INCORRECT, gameException.getErrorCode());
  }

  @Test
  void testCorrectSideSecondPlayer_IsValid() {
    gameState.setCurrentPlayer(PlayerType.SECOND_PLAYER);
    Unit reng = new Knight(gameState.getCurrentPlayer());
    PlaceUnitEvent event =
        new PlaceUnitEvent(0, 2, reng, gameState.getCurrentPlayer(), true, false);
    assertDoesNotThrow(() -> gameState.makePlacement(event));
  }

  @Test
  void testFullBoard_IsValide() {
    Board board = gameState.getCurrentBoard();
    Unit knight = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight2 = new Mage(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Healer(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight4 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight5 = new Mage(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight6 = new Healer(PlayerType.FIRST_PLAYER);
    board.setUnit(1, 0, firstPlayerKnight2);
    board.setUnit(2, 0, firstPlayerKnight3);
    board.setUnit(0, 1, firstPlayerKnight4);
    board.setUnit(1, 1, firstPlayerKnight5);
    board.setUnit(2, 1, firstPlayerKnight6);
    // Юзер напсисал такую строку
    // 0 0 Knight end где end это означает что он считает что он закончил
    PlaceUnitEvent event =
        new PlaceUnitEvent(0, 0, knight, gameState.getCurrentPlayer(), false, true);
    assertDoesNotThrow(() -> gameState.makePlacement(event));
  }

  @Test
  void testHaveGeneral_IsNotValide() throws GameException {

    // First palyer
    final Unit firstPlayerArcher = new Archer(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerMage = new Mage(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerHealer = new Healer(PlayerType.FIRST_PLAYER);
    firstPlayerArcher.setCurrentHp(0);

    PlaceUnitEvent place1 =
        new PlaceUnitEvent(0, 1, firstPlayerKnight1, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place2 =
        new PlaceUnitEvent(1, 1, firstPlayerKnight2, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place3 =
        new PlaceUnitEvent(2, 1, firstPlayerKnight3, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place4 =
        new PlaceUnitEvent(0, 0, firstPlayerArcher, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place5 =
        new PlaceUnitEvent(1, 0, firstPlayerMage, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place6 =
        new PlaceUnitEvent(2, 0, firstPlayerHealer, gameState.getCurrentPlayer(), false, false);

    gameState.makePlacement(place1);
    gameState.makePlacement(place2);
    gameState.makePlacement(place3);
    gameState.makePlacement(place4);
    gameState.makePlacement(place5);

    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(place6));
    assertEquals(ErrorCode.GENERAL_IS_MISSING, gameException.getErrorCode());
  }

  @Test
  void testFullBoard_IsNotValide() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Mage(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight1 = new Healer(PlayerType.FIRST_PLAYER);
    Unit secondPlayerKnight2 = new Archer(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight3 = new Healer(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);
    Unit archer = new Archer(gameState.getCurrentPlayer());
    // inProcess false это значит что пользовател решил что он закочил расстановку
    // Написал например end в конце строки или нажал на кнопку что закончил UI
    PlaceUnitEvent event =
        new PlaceUnitEvent(0, 0, archer, gameState.getCurrentPlayer(), false, false);
    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(event));
    assertEquals(ErrorCode.BOARD_IS_NOT_FULL, gameException.getErrorCode());
  }

  @Test
  public void testGeneralsNumber_IsNotValid() throws GameException {

    // First palyer
    final Unit firstPlayerArcher = new Archer(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerMage = new Mage(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerHealer = new Healer(PlayerType.FIRST_PLAYER);
    firstPlayerArcher.setCurrentHp(0);

    PlaceUnitEvent place1 =
        new PlaceUnitEvent(0, 1, firstPlayerKnight1, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place2 =
        new PlaceUnitEvent(1, 1, firstPlayerKnight2, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place3 =
        new PlaceUnitEvent(2, 1, firstPlayerKnight3, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place4 =
        new PlaceUnitEvent(0, 0, firstPlayerArcher, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place5 =
        new PlaceUnitEvent(1, 0, firstPlayerMage, gameState.getCurrentPlayer(), true, true);
    PlaceUnitEvent place6 =
        new PlaceUnitEvent(2, 0, firstPlayerHealer, gameState.getCurrentPlayer(), false, true);

    gameState.makePlacement(place1);
    gameState.makePlacement(place2);
    gameState.makePlacement(place3);
    gameState.makePlacement(place4);
    gameState.makePlacement(place5);

    GameException gameException =
        assertThrows(GameException.class, () -> gameState.makePlacement(place6));
    assertEquals(ErrorCode.TOO_MANY_GENERAL, gameException.getErrorCode());
  }
}
