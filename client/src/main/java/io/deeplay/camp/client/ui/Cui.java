package io.deeplay.camp.client.ui;

import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.entities.Unit;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Cui {
  String separator = System.lineSeparator();
  JFrame frame;
  JTextArea field;
  JPanel content;

  public Cui() {
    frame = new JFrame();
    frame.setSize(800, 500);
    field = new JTextArea(20, 50);
    field.setFont(new Font("Dialog", Font.PLAIN, 14));
    field.setTabSize(10);
    content = new JPanel();
    content.add(field);
    frame.add(content);
    frame.setVisible(true);
  }

  public void updateCui(Board board) {
    outInFrame(board);
  }

  public void outInFrame(Board board) {

    field.setText(null);
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
