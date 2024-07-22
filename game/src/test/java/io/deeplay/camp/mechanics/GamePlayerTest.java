package io.deeplay.camp.mechanics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.deeplay.camp.entities.Archer;
import io.deeplay.camp.entities.Board;
import io.deeplay.camp.entities.Healer;
import io.deeplay.camp.entities.Knight;
import io.deeplay.camp.entities.Mage;
import io.deeplay.camp.entities.Position;
import io.deeplay.camp.entities.Unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GamePlayerTest {
  private GameState gameState;

  @BeforeEach
  void setUp() {
    gameState = new GameState();
  }

  @Test
  void testEnumerationFirstPlayerUnits_IsValid() {
    PlayerType playerType = PlayerType.FIRST_PLAYER;
    Board board = gameState.getCurrentBoard();

    Unit firstPlayerKnight1 = new Knight(playerType);
    Unit firstPlayerKnight2 = new Knight(playerType);
    Unit firstPlayerKnight3 = new Knight(playerType);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    List<Position> unitPositions = GamePlayer.enumerationPlayerUnits(playerType, board);

    List<Position> expectrdPositions =
        List.of(new Position(0, 1), new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnits_IsValid() {
    PlayerType playerType = PlayerType.SECOND_PLAYER;
    Board board = gameState.getCurrentBoard();

    Unit secondPlayerKnight1 = new Knight(playerType);
    Unit secondPlayerKnight2 = new Knight(playerType);
    Unit secondPlayerKnight3 = new Knight(playerType);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);
    List<Position> unitPositions = GamePlayer.enumerationPlayerUnits(playerType, board);

    List<Position> expectrdPositions =
        List.of(new Position(0, 2), new Position(1, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayersUnitsWhenHaveSecondPlayer_IsValid() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    List<Position> unitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);

    List<Position> expectrdPositions =
        List.of(new Position(0, 1), new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnitsWhenHaveFirstPlayer_IsValid() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    List<Position> unitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    List<Position> expectrdPositions =
        List.of(new Position(0, 2), new Position(1, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayerUnitsWhenUnitsDead_IsValid() {
    PlayerType playerType = PlayerType.FIRST_PLAYER;
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight1 = new Knight(playerType);
    Unit firstPlayerKnight2 = new Knight(playerType);
    Unit firstPlayerKnight3 = new Knight(playerType);
    firstPlayerKnight2.setNowHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    List<Position> unitPositions = GamePlayer.enumerationPlayerUnits(playerType, board);
    List<Position> expectrdPositions = List.of(new Position(0, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayerUnitsWhenUnitsDead_IsValid() {
    PlayerType playerType = PlayerType.SECOND_PLAYER;
    Board board = gameState.getCurrentBoard();
    Unit secondPlayerKnight1 = new Knight(playerType);
    Unit secondPlayerKnight2 = new Knight(playerType);
    Unit secondPlayerKnight3 = new Knight(playerType);
    secondPlayerKnight2.setNowHp(0);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);
    List<Position> unitPositions = GamePlayer.enumerationPlayerUnits(playerType, board);
    List<Position> expectrdPositions = List.of(new Position(0, 2), new Position(2, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstPlayersUnitsWhenHaveSecondPlayerAndDeadUnits_IsValid() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    firstPlayerKnight1.setNowHp(0);
    Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    secondPlayerKnight3.setNowHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    List<Position> unitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);

    List<Position> expectrdPositions = List.of(new Position(1, 1), new Position(2, 1));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationSecondPlayersUnitsWhenHaveFirstPlayerAndDeadUnits_IsValid() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerKnight2 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight3 = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight1 = new Knight(PlayerType.FIRST_PLAYER);
    firstPlayerKnight1.setNowHp(0);
    Unit secondPlayerKnight2 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight3 = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerKnight1 = new Knight(PlayerType.SECOND_PLAYER);
    secondPlayerKnight3.setNowHp(0);
    board.setUnit(0, 1, firstPlayerKnight1);
    board.setUnit(1, 1, firstPlayerKnight2);
    board.setUnit(2, 1, firstPlayerKnight3);
    board.setUnit(0, 2, secondPlayerKnight1);
    board.setUnit(1, 2, secondPlayerKnight2);
    board.setUnit(2, 2, secondPlayerKnight3);

    List<Position> unitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    List<Position> expectrdPositions = List.of(new Position(0, 2), new Position(1, 2));

    assertEquals(expectrdPositions.size(), unitPositions.size());
    assertTrue(unitPositions.containsAll(expectrdPositions));
  }

  @Test
  void testEnumerationFirstAndSecondPlayersHaveUnitsAndDeadUnits_IsValid() {
    Board board = gameState.getCurrentBoard();
    Unit firstPlayerArcher = new Archer(PlayerType.FIRST_PLAYER);
    Unit firstPlayerMage = new Mage(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerHealer = new Healer(PlayerType.FIRST_PLAYER);
    firstPlayerArcher.setNowHp(0);
    board.setUnit(0, 0, firstPlayerArcher);
    board.setUnit(2, 0, firstPlayerMage);
    board.setUnit(2, 1, firstPlayerHealer);
    board.setUnit(1, 1, firstPlayerKnight);

    Unit secondPlayerKnight = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerArcher = new Archer(PlayerType.SECOND_PLAYER);
    Unit secondPlayerMage1 = new Mage(PlayerType.SECOND_PLAYER);
    Unit secondPlayerMage2 = new Mage(PlayerType.SECOND_PLAYER);
    Unit secondPlayerHealer = new Healer(PlayerType.SECOND_PLAYER);
    secondPlayerHealer.setNowHp(0);
    board.setUnit(1, 2, secondPlayerKnight);
    board.setUnit(2, 3, secondPlayerMage1);
    board.setUnit(0, 3, secondPlayerMage2);
    board.setUnit(2, 2, secondPlayerArcher);
    board.setUnit(1, 3, secondPlayerHealer);

    List<Position> firstPlayerUnitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.FIRST_PLAYER, board);
    List<Position> secondPlayerUnitPositions =
        GamePlayer.enumerationPlayerUnits(PlayerType.SECOND_PLAYER, board);

    List<Position> expectrdPositionsFirstPlayer =
        List.of(new Position(2, 1), new Position(2, 0), new Position(1, 1));
    List<Position> expectrdPositionsSecondPlayer =
        List.of(new Position(2, 3), new Position(1, 2), new Position(2, 2), new Position(0, 3));

    assertEquals(expectrdPositionsFirstPlayer.size(), firstPlayerUnitPositions.size());
    assertEquals(expectrdPositionsSecondPlayer.size(), secondPlayerUnitPositions.size());
    assertTrue(firstPlayerUnitPositions.containsAll(expectrdPositionsFirstPlayer));
    assertTrue(secondPlayerUnitPositions.containsAll(expectrdPositionsSecondPlayer));
  }

  @Test
  public void testStructurePossibleActions() {
    PossibleActions<Position, Position> map = new PossibleActions<Position, Position>();
    Position attackPositionUnits1 = new Position(1, 1);
    Position attackPositionUnits2 = new Position(1, 2);

    Position defencePositionUnits2 = new Position(0, 2);
    Position defencePositionUnits1 = new Position(1, 2);
    Position defencePositionUnits3 = new Position(2, 2);
    Position defencePositionUnits4 = new Position(0, 1);
    Position defencePositionUnits5 = new Position(1, 1);
    Position defencePositionUnits6 = new Position(2, 1);
    map.put(attackPositionUnits1, defencePositionUnits1);
    map.put(attackPositionUnits1, defencePositionUnits2);
    map.put(attackPositionUnits1, defencePositionUnits3);
    map.put(attackPositionUnits2, defencePositionUnits4);
    map.put(attackPositionUnits2, defencePositionUnits5);
    map.put(attackPositionUnits2, defencePositionUnits6);

    List<Position> allUnitsDefence1 = new ArrayList<>();
    allUnitsDefence1.add(defencePositionUnits1);
    allUnitsDefence1.add(defencePositionUnits2);
    allUnitsDefence1.add(defencePositionUnits3);
    List<Position> upload1 = map.get(attackPositionUnits1);

    List<Position> allUnitsDefence2 = new ArrayList<>();
    allUnitsDefence2.add(defencePositionUnits4);
    allUnitsDefence2.add(defencePositionUnits5);
    allUnitsDefence2.add(defencePositionUnits6);
    List<Position> upload2 = map.get(attackPositionUnits2);

    assertEquals(allUnitsDefence1.size(), upload1.size());
    assertEquals(allUnitsDefence2.size(), upload2.size());
    assertTrue(upload1.containsAll(allUnitsDefence1));
    assertTrue(upload2.containsAll(allUnitsDefence2));
    assertFalse(upload1.containsAll(allUnitsDefence2));
    assertFalse(upload2.containsAll(allUnitsDefence1));
  }

  @Test
  public void testUnitsPossibleActions() {
    Board board = gameState.getCurrentBoard();
    // First palyer
    Unit firstPlayerArcher = new Archer(PlayerType.FIRST_PLAYER);
    Unit firstPlayerMage = new Mage(PlayerType.FIRST_PLAYER);
    Unit firstPlayerKnight = new Knight(PlayerType.FIRST_PLAYER);
    Unit firstPlayerHealer = new Healer(PlayerType.FIRST_PLAYER);
    firstPlayerArcher.setNowHp(0);
    board.setUnit(0, 0, firstPlayerArcher);
    board.setUnit(2, 0, firstPlayerMage);
    board.setUnit(2, 1, firstPlayerHealer);
    board.setUnit(1, 1, firstPlayerKnight);
    // Возможные атаки рыцаря
    Position knight = new Position(1, 1);
    Position position1 = new Position(1, 2);
    Position position2 = new Position(2, 2);
    // Возможные атаки(лечение) хилера
    Position healer = new Position(2, 1);
    Position position3 = new Position(1, 1);
    // Себя тоже можно хилить
    Position position4 = new Position(2, 1);
    Position position9 = new Position(2, 0);

    // Возможные атаки мага
    Position mage = new Position(2, 0);
    Position position5 = new Position(0, 3);
    Position position6 = new Position(1, 2);
    Position position7 = new Position(2, 2);
    Position position8 = new Position(2, 3);
    // Лучник мёртв у него не должно быть атак проверить assertFalse обращаясь к нему по ключу
    Position archer = new Position(0, 0);

    Unit secondPlayerKnight = new Knight(PlayerType.SECOND_PLAYER);
    Unit secondPlayerArcher = new Archer(PlayerType.SECOND_PLAYER);
    Unit secondPlayerMage1 = new Mage(PlayerType.SECOND_PLAYER);
    Unit secondPlayerMage2 = new Mage(PlayerType.SECOND_PLAYER);
    Unit secondPlayerHealer = new Healer(PlayerType.SECOND_PLAYER);
    secondPlayerHealer.setNowHp(0);
    board.setUnit(1, 2, secondPlayerKnight);
    board.setUnit(2, 3, secondPlayerMage1);
    board.setUnit(0, 3, secondPlayerMage2);
    board.setUnit(2, 2, secondPlayerArcher);
    board.setUnit(1, 3, secondPlayerHealer);

    PossibleActions<Position, Position> positionPossibleActions =
        GamePlayer.unitsPossibleActions(gameState);

    List<Position> firstPlayerKnightAttacks = new ArrayList<>();
    firstPlayerKnightAttacks.add(position1);
    firstPlayerKnightAttacks.add(position2);
    List<Position> firstPlayerHealerAttacks = new ArrayList<>();
    firstPlayerHealerAttacks.add(position3);
    firstPlayerHealerAttacks.add(position4);
    firstPlayerHealerAttacks.add(position9);
    List<Position> firstPlayerMageAttacks = new ArrayList<>();
    firstPlayerMageAttacks.add(position5);
    firstPlayerMageAttacks.add(position6);
    firstPlayerMageAttacks.add(position7);
    firstPlayerMageAttacks.add(position8);

    assertEquals(firstPlayerKnightAttacks.size(), positionPossibleActions.get(knight).size());
    assertEquals(firstPlayerHealerAttacks.size(), positionPossibleActions.get(healer).size());
    assertEquals(firstPlayerMageAttacks.size(), positionPossibleActions.get(mage).size());
    assertTrue(positionPossibleActions.get(knight).containsAll(firstPlayerKnightAttacks));
    assertTrue(positionPossibleActions.get(healer).containsAll(firstPlayerHealerAttacks));
    assertTrue(positionPossibleActions.get(mage).containsAll(firstPlayerMageAttacks));
    assertFalse(positionPossibleActions.get(archer).containsAll(firstPlayerMageAttacks));
  }
}
