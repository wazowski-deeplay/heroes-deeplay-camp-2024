package io.deeplay.camp.botfarm;


import io.deeplay.camp.botfarm.bots.RandomBot;
import io.deeplay.camp.game.exceptions.GameException;

public class Main {
  public static void main(String[] args) {

    RandomBot bot1 = new RandomBot();
    RandomBot bot2 = new RandomBot();
    BotFight fight = new BotFight(bot1, bot2, 30 , true);
    try {
      fight.playGames();
    } catch (GameException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
