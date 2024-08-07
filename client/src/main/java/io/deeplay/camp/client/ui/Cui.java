package io.deeplay.camp.client.ui;

import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import java.awt.Font;
import java.util.UUID;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Cui {
  String separator = System.lineSeparator();
  JFrame frame;
  JTextArea field;
  JPanel content;
  PlayerType gamePlayer;

  public Cui(PlayerType playerType) {
    frame = new JFrame();
    frame.setSize(800, 500);
    field = new JTextArea(20, 50);
    field.setFont(new Font("Dialog", Font.PLAIN, 14));
    field.setTabSize(10);
    content = new JPanel();
    content.add(field);
    frame.add(content);
    frame.setVisible(true);
    this.gamePlayer = playerType;
  }

  public void updateCui(GameState gameState, UUID id, PlayerType playerType) {
    gamePlayer = playerType;
    outInFrame(gameState, id);
  }
  public void cleanCui(UUID id, PlayerType playerType) {
    gamePlayer = playerType;
    outInFrame(null, id);
  }

  public void outInFrame(GameState gameState, UUID idRoom) {
    Board board = null;
    if(gameState == null){
      board = new Board();
    }
    else{
      board = gameState.getCurrentBoard();
    }
    field.setText(null);
    field.append("ID room: " + idRoom.toString());
    field.append(separator);
    if (gameState != null) {
      field.append("Current player: " + gameState.getCurrentPlayer().toString());
    }
    field.append(separator);
    field.append("SECOND_PLAYER");
    field.append(separator);
    field.append(separator);
    String s = "20";
    for (int row = 3; row >= 0; row--) {
      field.append(String.format("%-" + s + "d", row));
      for (int column = 0; column < 3; column++) {
        field.append(
            String.format(
                "%-" + s + "s",
                outUnitIsMoved(board.getUnit(column, row))
                    + outUnitIsGeneral(board.getUnit(column, row))
                    + outUnitInfo(board.getUnit(column, row))));
      }
      field.append(separator);
      field.append(separator);
    }
    field.append(String.format("%-25s", "#"));
    field.append(String.format("%-25s", "0"));
    field.append(String.format("%-27s", "1"));
    field.append(String.format("%-26s", "2"));
    field.append(separator);
    field.append(separator);
    field.append("FIRST_PLAYER");
    field.append(separator);
    field.append(separator);
    field.append("You are " + gamePlayer.name());
    field.append(separator);
    field.append(separator);
    if(gameState != null){
      if (gameState.getGameStage() == GameStage.ENDED) {
        field.append("Winner = " + gameState.getWinner().name());
        System.out.println("Победитель = " + gameState.getWinner().name());
        field.append(separator);
        field.append(separator);
      }
    }
  }

  public void downCuiFrame() {
    field = null;
    content = null;
    frame.setVisible(false);
    frame = null;
  }

  // Методы для отображения стринговой информации о юните
  private String outUnitIsMoved(Unit unit) {
    String result = "?";
    if (unit == null) {
      return "";
    }
    if (unit.getMoved()) {
      result = "!";
    }
    return result;
  }

  private String outUnitIsGeneral(Unit unit) {
    String result = "";
    if (unit == null) {
      return "";
    }
    if (unit.isGeneral()) {
      result = "G";
    }
    return result;
  }

  private String outUnitInfo(Unit unit) {
    String result = "?";
    if (unit == null) {
      return result = "------";
    }
    switch (unit.getUnitType()) {
      case KNIGHT -> result = "Knight" + unit.getCurrentHp();
      case ARCHER -> result = "Archer" + unit.getCurrentHp();
      case MAGE -> result = "Wizard" + unit.getCurrentHp();
      case HEALER -> result = "Healer" + unit.getCurrentHp();
      default -> result = "------";
    }
    return result;
  }
}
