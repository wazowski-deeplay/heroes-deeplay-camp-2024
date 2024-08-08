package io.deeplay.camp.botfarm;


import io.deeplay.camp.botfarm.bots.RandomBot;
import io.deeplay.camp.game.exceptions.GameException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  static String path = "C:\\Deeplay\\deeplay-heroes\\botfarm\\src\\main\\java\\io\\deeplay\\camp\\botfarm";

  public static void main(String[] args) throws IOException {

    deleteFilesForPathByPrefix(path, "resultgame");

    RandomBot bot1 = new RandomBot();
    RandomBot bot2 = new RandomBot();
    BotFight fight1 = new BotFight(bot1, bot2, 5, true);
    BotFight fight2 = new BotFight(bot1, bot2, 5, true);
    BotFight fight3 = new BotFight(bot1, bot2, 5, true);
    fight1.start();
    fight2.start();
    fight3.start();
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
