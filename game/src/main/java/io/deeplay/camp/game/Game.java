package io.deeplay.camp.game;

import io.deeplay.camp.game.entities.Army;
import io.deeplay.camp.game.entities.Board;
import io.deeplay.camp.game.events.ChangePlayerEvent;
import io.deeplay.camp.game.events.GiveUpEvent;
import io.deeplay.camp.game.events.MakeMoveEvent;
import io.deeplay.camp.game.events.PlaceUnitEvent;
import io.deeplay.camp.game.events.StartGameEvent;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.GameStage;
import io.deeplay.camp.game.mechanics.GameState;
import io.deeplay.camp.game.mechanics.PlayerType;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
public class Game implements GameListener {

  private GameState gameState;

  private static final Logger logger = LoggerFactory.getLogger(GameState.class);

  public Game() {
    gameState = new GameState();
  }

  @Override
  public void startGame(StartGameEvent startGameEvent) {}

  @Override
  public void placeUnit(PlaceUnitEvent placeUnitEvent) throws GameException {
    gameState.makePlacement(placeUnitEvent);
  }

  @Override
  public void changePlayer(ChangePlayerEvent changePlayerEvent) throws GameException {
    gameState.makeChangePlayer(changePlayerEvent);
  }

  @Override
  public void makeMove(MakeMoveEvent makeMoveEvent) throws GameException {
    gameState.makeMove(makeMoveEvent);
  }

  public void giveUp(GiveUpEvent giveUpEvent) throws GameException {
    gameState.giveUp(giveUpEvent);
  }
  public void draw(List<Boolean> value) throws GameException {
    gameState.draw(value);
  }

  public void restart(List<Boolean> value) throws GameException {
    if (value.get(0) && value.get(1)) {
      gameState = new GameState();
      logger.atInfo().log("Началась новая игра");
    }
  }
}
