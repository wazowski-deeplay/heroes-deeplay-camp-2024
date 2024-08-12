package io.deeplay.camp.client;

import io.deeplay.camp.core.dto.GameType;
import io.deeplay.camp.core.dto.client.ClientDto;
import io.deeplay.camp.core.dto.client.connection.DisconnectDto;
import io.deeplay.camp.core.dto.client.game.*;
import io.deeplay.camp.core.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.core.dto.client.party.ExitGamePartyDto;
import io.deeplay.camp.core.dto.client.party.GetPartiesDto;
import io.deeplay.camp.core.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.game.entities.UnitType;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class ParserRequest {

  public static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public ClientDto convert(
      String userWord,
      UUID gameSendPartyId,
      HashMap<Integer, UUID> ids,
      HashMap<UUID, GameStatePlayer> myGameParties) {
    ClientDto clientDto = null;
    String[] userCommand = userWord.split("\\s+");
    String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    Pattern pattern = Pattern.compile(regex);
    UUID gamePartyId = gameSendPartyId;
    int startIndexRead = 0;
    if (userCommand[0].equals("checkout")) {
      if (pattern.matcher(userCommand[1]).matches()) {
        clientDto = new SwitchPartyDto(UUID.fromString(userCommand[1]));
      } else if (isNumeric(userCommand[1])) {
        if (Integer.parseInt(userCommand[1]) > 0
            && Integer.parseInt(userCommand[1]) <= ids.size()) {
          clientDto = new SwitchPartyDto(ids.get(Integer.parseInt(userCommand[1])));
        }
      }
    }
    for (UUID idRoom : myGameParties.keySet()) {
      if (userCommand[0].equals(idRoom.toString().substring(0, 4))) {
        startIndexRead = 1;
        gamePartyId = idRoom;
        break;
      } else {
        startIndexRead = 0;
      }
    }
    if (userCommand[startIndexRead].equals("exitgame")) {
      if (userCommand.length != 1 + startIndexRead) {
        return null;
      }
      clientDto = new ExitGamePartyDto(gamePartyId);
      return clientDto;
    }
    if (userCommand[startIndexRead].equals("disconnect")) {
      if (userCommand.length != 1 + startIndexRead) {
        return null;
      }
      clientDto = new DisconnectDto(gamePartyId);
      return clientDto;
    }
    if (userCommand[startIndexRead].equals("makemove")) {
      for (int i = startIndexRead + 1; i < startIndexRead + 5; i++) {
        if (!isNumeric(userCommand[i])) {
          return null;
        }
      }
      if (userCommand.length != 5 + startIndexRead) {
        return null;
      }
      clientDto =
          new MakeMoveDto(
              gamePartyId,
              charToInt(userCommand[startIndexRead + 1]),
              charToInt(userCommand[startIndexRead + 2]),
              charToInt(userCommand[startIndexRead + 3]),
              charToInt(userCommand[startIndexRead + 4]));
      return clientDto;
    }
    if (userCommand[startIndexRead].equals("place")) {
      boolean inProcess = true;
      boolean general = false;
      if (!isNumeric(userCommand[startIndexRead + 1])
          || !isNumeric(userCommand[startIndexRead + 2])) {
        return null;
      }
      if (!isUnitType(userCommand[startIndexRead + 3])) {
        return null;
      }
      if (userCommand.length == startIndexRead + 5) {
        if (!userCommand[startIndexRead + 4].equals("end")) {
          if (!userCommand[startIndexRead + 4].equals("general")) {
            return null;
          } else {
            general = true;
          }
        } else {
          inProcess = false;
        }
      }
      if (userCommand.length == startIndexRead + 6) {
        if (!userCommand[startIndexRead + 4].equals("general")) {
          if (!userCommand[startIndexRead + 5].equals("end")) {
            return null;
          } else {
            inProcess = false;
          }
        } else {
          general = true;
        }
      }
      clientDto =
          new PlaceUnitDto(
              gamePartyId,
              charToInt(userCommand[startIndexRead + 1]),
              charToInt(userCommand[startIndexRead + 2]),
              getUnitType(userCommand[startIndexRead + 3]),
              inProcess,
              general);
    }
    if (userCommand[startIndexRead].equals("endturn")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new ChangePlayerDto(gamePartyId);
    }
    if (userCommand[startIndexRead].equals("creategame")) {
      if (userCommand[startIndexRead + 1].equals("vs_human")) {
        clientDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
      } else if (userCommand[startIndexRead + 1].equals("vs_bot")) {
        clientDto = new CreateGamePartyDto(GameType.HUMAN_VS_BOT);
      } else if (userCommand[startIndexRead + 1].equals("bot_vs_bot")) {
        clientDto = new CreateGamePartyDto(GameType.BOT_VS_BOT);
      } else {
        return null;
      }
    }
    if (userCommand[startIndexRead].equals("joingame")) {
      if (userCommand.length != startIndexRead + 2) {
        return null;
      }
      if (pattern.matcher(userCommand[startIndexRead + 1]).matches()) {
        clientDto = new JoinGamePartyDto(UUID.fromString(userCommand[startIndexRead + 1]));
      } else if (isNumeric(userCommand[startIndexRead + 1])) {
        if (Integer.parseInt(userCommand[startIndexRead + 1]) > 0
            && Integer.parseInt(userCommand[startIndexRead + 1]) <= ids.size()) {
          clientDto =
              new JoinGamePartyDto(ids.get(Integer.parseInt(userCommand[startIndexRead + 1])));
        }
      }
    }
    if (userCommand[startIndexRead].equals("giveup")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new GiveUpDto(gamePartyId);
    }
    if (userCommand[startIndexRead].equals("offer_draw")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new OfferDrawDto(gamePartyId);
    }
    if (userCommand[startIndexRead].equals("draw")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new DrawDto(gamePartyId);
    }
    if (userCommand[startIndexRead].equals("getparties")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new GetPartiesDto();
    }
    if (userCommand[startIndexRead].equals("offer_restart")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new OfferRestartDto(gamePartyId);
    }
    if (userCommand[startIndexRead].equals("restart")) {
      if (userCommand.length != startIndexRead + 1) {
        return null;
      }
      clientDto = new RestartDto(gamePartyId);
    }
    return clientDto;
  }

  private int charToInt(String str) {
    return Integer.parseInt(str);
  }

  private boolean isUnitType(String str) {
    switch (str) {
      case "knight" -> {
        return true;
      }
      case "archer" -> {
        return true;
      }
      case "mage" -> {
        return true;
      }
      case "healer" -> {
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  private UnitType getUnitType(String str) {
    switch (str) {
      case "knight" -> {
        return UnitType.KNIGHT;
      }
      case "archer" -> {
        return UnitType.ARCHER;
      }
      case "mage" -> {
        return UnitType.MAGE;
      }
      case "healer" -> {
        return UnitType.HEALER;
      }
      default -> System.out.println("gay type");
    }
    return null;
  }
}
