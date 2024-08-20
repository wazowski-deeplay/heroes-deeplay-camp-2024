package io.deeplay.camp.botfarm;


import io.deeplay.camp.botfarm.bots.MinMaxBot;
import io.deeplay.camp.botfarm.bots.RandomBot;
import io.deeplay.camp.game.exceptions.GameException;
import io.deeplay.camp.game.mechanics.PlayerType;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  static String path =
      "C:\\Users\\Maksim\\IdeaProjects\\ruf-heroes-deeplay-camp-2024\\botfarm\\src\\main\\java\\io\\deeplay\\camp\\botfarm";

  public static void main(String[] args) throws IOException {

    deleteFilesForPathByPrefix(path, "resultgame");

    MinMaxBot bot1 = new MinMaxBot(3, PlayerType.FIRST_PLAYER);
    // RandomBot bot1 = new RandomBot();
    // MinMaxBot bot2 = new MinMaxBot(3, PlayerType.SECOND_PLAYER);
    RandomBot bot2 = new RandomBot();
    for(int i = 0; i<1;i++){
      BotFight fight = new BotFight(bot1 , bot2, 100, true);
    }
  }

  public static boolean deleteFilesForPathByPrefix(final String path, final String prefix) {
    boolean success = true;
    try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(path), prefix + "*")) {
      for (final Path newDirectoryStreamItem : newDirectoryStream) {
        Files.delete(newDirectoryStreamItem);
      }
    } catch (final Exception e) {
      success = false;
      e.printStackTrace();
    }
    return success;
  }
}
