package io.deeplay.camp.botfarm;

import io.deeplay.camp.botfarm.bots.RandomBot;
import io.deeplay.camp.game.Game;
import io.deeplay.camp.game.entities.Unit;
import io.deeplay.camp.game.entities.UnitType;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;

import java.awt.Font;
import java.io.IOException;
import java.util.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BotFight extends Thread{

    private static int winsFirstPlayer = 0;
    private static int winsSecondPlayer = 0;
    private static int countDraw = 0;
    private final int countGame;

    private final int timeSkeep = 50;
    Game game;
    GameAnalisys gameAnalisys;
    RandomBot botFirst;
    RandomBot botSecond;
    boolean consoleOut = true;
    boolean outInfoGame;
    final Timer timer = new Timer();

    String separator = System.getProperty("line.separator");

    int fightId;

    JFrame frame;
    JTextArea area1;
    JPanel contents;

    Thread threadFight;
    TimerForBot timerForBot;


    public BotFight(RandomBot botFirst, RandomBot botSecond, int countGame, boolean infoGame) throws IOException {
        this.botFirst = botFirst;
        this.botSecond = botSecond;
        this.countGame = countGame;
        fightId = (int)(100000+Math.random()*999999);
        gameAnalisys = new GameAnalisys(countGame, fightId);
        this.outInfoGame = infoGame;
        threadFight = new Thread(this);

        timerForBot = new TimerForBot(threadFight, timer);
        timer.schedule(timerForBot, 30000);

        frame = new JFrame();
        frame.setSize(800, 500);
        area1 = new JTextArea(20, 50);
        area1.setFont(new Font("Dialog", Font.PLAIN, 14));
        area1.setTabSize(10);
        contents = new JPanel();
        contents.add(area1);
        frame.add(contents);
        frame.setVisible(true);

        threadFight.start();

    }

    @Override
    public void run() {
        try {
            playGames();
            frame.setVisible(false);
            threadFight.interrupt();
        } catch (GameException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void playGames() throws GameException, InterruptedException, IOException {
        for (int gameCount = 0; gameCount < countGame; gameCount++) {

            game = new Game();
            executePlace(game.getGameState(), gameCount);
            game.getGameState().changeCurrentPlayer();
            executePlace(game.getGameState(), gameCount);
            game.getGameState().changeCurrentPlayer();
            gameAnalisys.setCurrentBoard(game.getGameState().getCurrentBoard().getUnits());

            while (game.getGameState().getGameStage() != GameStage.ENDED) {
                executeMove(game.getGameState(), gameCount);
                game.changePlayer(new ChangePlayerEvent(game.getGameState().getCurrentPlayer()));
                executeMove(game.getGameState(), gameCount);
                game.changePlayer(new ChangePlayerEvent(game.getGameState().getCurrentPlayer()));
            }

            game = null;
        }

        if (outInfoGame) {
            gameAnalisys.outputInfo();
        }
    }

    public void executeMove(GameState gameState, int countGame)
            throws GameException, InterruptedException {
        for (int i = 0; i < 6; i++) {
            if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
                MakeMoveEvent event = botFirst.generateMakeMoveEvent(gameState);
                if (event == null) {
                    continue;
                }
                game.makeMove(event);
                Thread.sleep(timeSkeep);
                outInFrame(event);
            } else {
                MakeMoveEvent event = botSecond.generateMakeMoveEvent(gameState);
                if (event == null) {
                    continue;
                }
                game.makeMove(event);
                Thread.sleep(timeSkeep);
                outInFrame(event);
            }
        }
    }

    public void executePlace(GameState gameState, int countGame)
            throws GameException, InterruptedException {
        for (int i = 0; i < 6; i++) {
            if (gameState.getCurrentPlayer() == PlayerType.FIRST_PLAYER) {
                game.placeUnit(botFirst.generatePlaceUnitEvent(gameState));
                Thread.sleep(timeSkeep);
                outInFrame(null);
            } else {
                game.placeUnit(botSecond.generatePlaceUnitEvent(gameState));
                Thread.sleep(timeSkeep);
                outInFrame(null);
            }
        }
    }

    // Вывод в окно JFrame
    public void outInFrame(MakeMoveEvent move) {
        if (consoleOut) {
            area1.setText(null);
            if (move == null) {
                area1.append("BEGIN NEW GAME!");
            }
            area1.append(separator);
            area1.append(separator);
            String s = "20";
            for (int row = 3; row >= 0; row--) {
                area1.append(String.format("%-" + s + "d", row));
                for (int column = 0; column < 3; column++) {
                    area1.append(
                            String.format(
                                    "%-" + s + "s",
                                    outUnitIsMoved(game.getGameState().getCurrentBoard().getUnit(column, row))
                                            + outUnitInfo(game.getGameState().getCurrentBoard().getUnit(column, row))));
                }
                area1.append(separator);
                area1.append(separator);
            }

            area1.append(String.format("%-25s", "#"));
            area1.append(String.format("%-25s", "0"));
            area1.append(String.format("%-27s", "1"));
            area1.append(String.format("%-26s", "2"));
            area1.append(separator);
            area1.append(separator);

            if (move != null) {
                area1.append(
                        outUnitMove(
                                move.getAttacker().getUnitType(),
                                move.getFrom().x(),
                                move.getFrom().y(),
                                move.getTo().x(),
                                move.getTo().y()));
            }
        }
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

    private String outUnitMove(UnitType unitType, int fromX, int fromY, int toX, int toY) {
        if (unitType == UnitType.MAGE) {
            return "Unit Mage" + "(" + fromX + "," + fromY + ") attack all enemys units";
        } else {
            String action;
            if (unitType != UnitType.HEALER) {
                action = " attack ";
            } else {
                action = " heal ";
            }
            return "Unit "
                    + unitType.name()
                    + "("
                    + fromX
                    + ","
                    + fromY
                    + ")"
                    + action
                    + game.getGameState().getCurrentBoard().getUnit(toX, toY).getUnitType().name()
                    + "("
                    + toX
                    + ","
                    + toY
                    + ")";
        }
    }
}
