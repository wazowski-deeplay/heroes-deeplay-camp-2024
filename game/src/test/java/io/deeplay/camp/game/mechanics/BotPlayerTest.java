package io.deeplay.camp.game.mechanics;

import static org.junit.jupiter.api.Assertions.*;

import io.deeplay.camp.game.entities.Archer;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Healer;
import io.deeplay.camp.game.entities.Knight;
import io.deeplay.camp.game.entities.Mage;
import io.deeplay.camp.game.entities.Position;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.exceptions.GameException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BotPlayerTest {
  private GameState gameState;
  private BotPlayer botPlayer;

  @BeforeEach
  void setUp() {
    gameState = new GameState();
    botPlayer = new BotPlayer();
  }

  @Test
  void testEnumerationFirstPlayerUnits_IsValid() {
    final PlayerType playerType = PlayerType.FIRST_PLAYER;
    final Board board = gameState.getCurrentBoard();

    final Unit firstPlayerKnight1 = new Knight(playerType);
    final Unit firstPlayerKnight2 = new Knight(playerType);
    final Unit firstPlayerKnight3 = new Knight(playerType);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    final List<Position> unitPositions = botPlayer.enumerationPlayerUnits(playerType, board);

    final List<Position> expectrdPositions =
        List.of(new Position(0, 1), new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnits_IsValid() {
    final PlayerType playerType = PlayerType.SECOND_PLAYER;
    final Board board = gameState.getCurrentBoard();

    final Unit secondPlayerKnight1 = new Knight(playerType);
    final Unit secondPlayerKnight2 = new Knight(playerType);
    final Unit secondPlayerKnight3 = new Knight(playerType);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);
    final List<Position> unitPositions = botPlayer.enumerationPlayerUnits(playerType, board);

    final List<Position> expectrdPositions =
        List.of(new Position(0, 2), new Position(1, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayersUnitsWhenHaveSecondPlayer_IsValid() {
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    final List<Position> unitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);

    final List<Position> expectrdPositions =
        List.of(new Position(0, 1), new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnitsWhenHaveFirstPlayer_IsValid() {
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    final List<Position> unitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    final List<Position> expectrdPositions =
        List.of(new Position(0, 2), new Position(1, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayerUnitsWhenUnitsDead_IsValid() {
    final PlayerType playerType = PlayerType.FIRST_PLAYER;
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerKnight1 = new Knight(playerType);
    final Unit firstPlayerKnight2 = new Knight(playerType);
    final Unit firstPlayerKnight3 = new Knight(playerType);
    firstPlayerKnight2.setCurrentHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    final List<Position> unitPositions = botPlayer.enumerationPlayerUnits(playerType, board);
    final List<Position> expectrdPositions = List.of(new Position(0, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayerUnitsWhenUnitsDead_IsValid() {
    final PlayerType playerType = PlayerType.SECOND_PLAYER;
    final Board board = gameState.getCurrentBoard();
    final Unit secondPlayerKnight1 = new Knight(playerType);
    final Unit secondPlayerKnight2 = new Knight(playerType);
    final Unit secondPlayerKnight3 = new Knight(playerType);
    secondPlayerKnight2.setCurrentHp(0);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);
    final List<Position> unitPositions = botPlayer.enumerationPlayerUnits(playerType, board);
    final List<Position> expectrdPositions = List.of(new Position(0, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayersUnitsWhenHaveSecondPlayerAndDeadUnits_IsValid() {
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    firstPlayerKnight1.setCurrentHp(0);
    final Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    secondPlayerKnight3.setCurrentHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    final List<Position> unitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);

    final List<Position> expectrdPositions = List.of(new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnitsWhenHaveFirstPlayerAndDeadUnits_IsValid() {
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    firstPlayerKnight1.setCurrentHp(0);
    final Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    secondPlayerKnight3.setCurrentHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    final List<Position> unitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    final List<Position> expectrdPositions = List.of(new Position(0, 2), new Position(1, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstAndSecondPlayersHaveUnitsAndDeadUnits_IsValid() {
    final Board board = gameState.getCurrentBoard();
    final Unit firstPlayerArcher = new Archer(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerMage = new Mage(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerKnight = new Knight(PlayerType.FIRST_PLAYER);
    final Unit firstPlayerHealer = new Healer(PlayerType.FIRST_PLAYER);
    firstPlayerArcher.setCurrentHp(0);
    board.setUnit(0, 0, firstPlayerArcher);
    board.setUnit(2, 0, firstPlayerMage);
    board.setUnit(2, 1, firstPlayerHealer);
    board.setUnit(1, 1, firstPlayerKnight);

    final Unit secondPlayerKnight = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerArcher = new Archer(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerMage1 = new Mage(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerMage2 = new Mage(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerHealer = new Healer(PlayerType.SECOND_PLAYER);
    secondPlayerHealer.setCurrentHp(0);
    board.setUnit(1, 2, secondPlayerKnight);
    board.setUnit(2, 3, secondPlayerMage1);
    board.setUnit(0, 3, secondPlayerMage2);
    board.setUnit(2, 2, secondPlayerArcher);
    board.setUnit(1, 3, secondPlayerHealer);

    final List<Position> firstPlayerUnitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
    final List<Position> secondPlayerUnitPositions =
        botPlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    final List<Position> expectrdPositionsFirstPlayer =
        List.of(new Position(2, 1), new Position(2, 0), new Position(1, 1));
    final List<Position> expectrdPositionsSecondPlayer =
        List.of(new Position(2, 3), new Position(1, 2), new Position(2, 2), new Position(0, 3));

    assertEquals(expectrdPositionsFirstPlayer.size(), firstPlayerUnitPositions.size());
    assertEquals(expectrdPositionsSecondPlayer.size(), secondPlayerUnitPositions.size());
    assertTrue(firstPlayerUnitPositions.containsAll(expectrdPositionsFirstPlayer));
    assertTrue(secondPlayerUnitPositions.containsAll(expectrdPositionsSecondPlayer));
  }

  @Test
  public void testStructurePossibleActions() {
    final PossibleActions<Position, Position> map = new PossibleActions<Position, Position>();
    final Position attackPositionUnits1 = new Position(1, 1);
    final Position attackPositionUnits2 = new Position(1, 2);

    final Position defencePositionUnits2 = new Position(0, 2);
    final Position defencePositionUnits1 = new Position(1, 2);
    final Position defencePositionUnits3 = new Position(2, 2);
    final Position defencePositionUnits4 = new Position(0, 1);
    final Position defencePositionUnits5 = new Position(1, 1);
    final Position defencePositionUnits6 = new Position(2, 1);
    map.put(attackPositionUnits1, defencePositionUnits1);
    map.put(attackPositionUnits1, defencePositionUnits2);
    map.put(attackPositionUnits1, defencePositionUnits3);
    map.put(attackPositionUnits2, defencePositionUnits4);
    map.put(attackPositionUnits2, defencePositionUnits5);
    map.put(attackPositionUnits2, defencePositionUnits6);

    final List<Position> allUnitsDefence1 = new ArrayList<>();
    allUnitsDefence1.add(defencePositionUnits1);
    allUnitsDefence1.add(defencePositionUnits2);
    allUnitsDefence1.add(defencePositionUnits3);
    final List<Position> upload1 = map.get(attackPositionUnits1);

    final List<Position> allUnitsDefence2 = new ArrayList<>();
    allUnitsDefence2.add(defencePositionUnits4);
    allUnitsDefence2.add(defencePositionUnits5);
    allUnitsDefence2.add(defencePositionUnits6);
    final List<Position> upload2 = map.get(attackPositionUnits2);

    assertEquals(allUnitsDefence1.size(), upload1.size());
    assertEquals(allUnitsDefence2.size(), upload2.size());
    assertTrue(upload1.containsAll(allUnitsDefence1));
    assertTrue(upload2.containsAll(allUnitsDefence2));
    assertFalse(upload1.containsAll(allUnitsDefence2));
    assertFalse(upload2.containsAll(allUnitsDefence1));
  }

  @Test
  public void testUnitsPossibleActions() throws GameException {

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
        new PlaceUnitEvent(2, 0, firstPlayerHealer, gameState.getCurrentPlayer(), false, true);

    gameState.makePlacement(place1);
    gameState.makePlacement(place2);
    gameState.makePlacement(place3);
    gameState.makePlacement(place4);
    gameState.makePlacement(place5);
    gameState.makePlacement(place6);

    gameState.changeCurrentPlayer();

    final Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerArcher = new Archer(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerMage = new Mage(PlayerType.SECOND_PLAYER);
    final Unit secondPlayerHealer = new Healer(PlayerType.SECOND_PLAYER);
    secondPlayerMage.setCurrentHp(0);

    PlaceUnitEvent place7 =
        new PlaceUnitEvent(0, 2, secondPlayerKnight1, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place8 =
        new PlaceUnitEvent(1, 2, secondPlayerKnight2, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place9 =
        new PlaceUnitEvent(2, 2, secondPlayerKnight3, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place10 =
        new PlaceUnitEvent(0, 3, secondPlayerArcher, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place11 =
        new PlaceUnitEvent(1, 3, secondPlayerMage, gameState.getCurrentPlayer(), true, false);
    PlaceUnitEvent place12 =
        new PlaceUnitEvent(2, 3, secondPlayerHealer, gameState.getCurrentPlayer(), false, true);

    gameState.makePlacement(place7);
    gameState.makePlacement(place8);
    gameState.makePlacement(place9);
    gameState.makePlacement(place10);
    gameState.makePlacement(place11);
    gameState.makePlacement(place12);

    gameState.changeCurrentPlayer();
    gameState.changeCurrentPlayer();
    gameState.changeCurrentPlayer();

    // Возможные атаки рыцаря 0 1 Первого игрока
    final Position knight = new Position(0, 1);
    final Position position1 = new Position(0, 2);
    final Position position2 = new Position(1, 2);
    // Возможные лечение хилера 2 0 Первого игрока
    final Position healer = new Position(2, 0);
    final Position position3 = new Position(1, 0);
    final Position position4 = new Position(2, 0);
    final Position position5 = new Position(0, 1);
    final Position position6 = new Position(1, 1);
    final Position position7 = new Position(2, 1);
    // Возможные атаки мага 1 0 Первого игрока
    final Position mage = new Position(1, 0);
    final Position position8 = new Position(0, 2);
    final Position position9 = new Position(1, 2);
    final Position position10 = new Position(2, 2);
    final Position position11 = new Position(0, 3);
    final Position position12 = new Position(2, 3);

    final PossibleActions<Position, Position> positionPossibleActions =
        botPlayer.unitsPossibleActions(gameState);

    final List<Position> firstPlayerKnightAttacks = new ArrayList<>();
    firstPlayerKnightAttacks.add(position1);
    firstPlayerKnightAttacks.add(position2);
    final List<Position> firstPlayerHealerAttacks = new ArrayList<>();
    firstPlayerHealerAttacks.add(position3);
    firstPlayerHealerAttacks.add(position4);
    firstPlayerHealerAttacks.add(position5);
    firstPlayerHealerAttacks.add(position6);
    firstPlayerHealerAttacks.add(position7);
    final List<Position> firstPlayerMageAttacks = new ArrayList<>();
    firstPlayerMageAttacks.add(position8);
    firstPlayerMageAttacks.add(position9);
    firstPlayerMageAttacks.add(position10);
    firstPlayerMageAttacks.add(position11);
    firstPlayerMageAttacks.add(position12);

    assertEquals(firstPlayerKnightAttacks.size(), positionPossibleActions.get(knight).size());
    assertEquals(firstPlayerHealerAttacks.size(), positionPossibleActions.get(healer).size());
    assertEquals(firstPlayerMageAttacks.size(), positionPossibleActions.get(mage).size());
    assertTrue(positionPossibleActions.get(knight).containsAll(firstPlayerKnightAttacks));
    assertTrue(positionPossibleActions.get(healer).containsAll(firstPlayerHealerAttacks));
    assertTrue(positionPossibleActions.get(mage).containsAll(firstPlayerMageAttacks));
  }
}
