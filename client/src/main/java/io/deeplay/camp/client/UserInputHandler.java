package io.deeplay.camp.client;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserInputHandler {

  public static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public UUID isUserHandler(String userWord, HashMap<Integer, UUID> ids) {
    String[] userCommand = userWord.split("\\s+");
    String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    Pattern pattern = Pattern.compile(regex);
    if (userCommand[0].equals("checkout")) {
      if (pattern.matcher(userCommand[1]).matches()) {
        return UUID.fromString(userCommand[1]);
      } else if (isNumeric(userCommand[1])) {
        if (Integer.parseInt(userCommand[1]) > 0
            && Integer.parseInt(userCommand[1]) <= ids.size()) {
          return ids.get(Integer.parseInt(userCommand[1]));
        }
      }
    }
    return null;
  }
}
