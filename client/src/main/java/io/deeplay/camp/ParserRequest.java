package io.deeplay.camp;

import io.deeplay.camp.dto.GameType;
import io.deeplay.camp.dto.client.ClientDto;
import io.deeplay.camp.dto.client.game.ChangePlayerDto;
import io.deeplay.camp.dto.client.game.GiveUpDto;
import io.deeplay.camp.dto.client.game.MakeMoveDto;
import io.deeplay.camp.dto.client.game.OfferGiveUpDto;
import io.deeplay.camp.dto.client.game.PlaceUnitDto;
import io.deeplay.camp.dto.client.party.CreateGamePartyDto;
import io.deeplay.camp.dto.client.party.JoinGamePartyDto;
import io.deeplay.camp.entities.UnitType;
import java.util.UUID;

public class ParserRequest {

  public ClientDto convert(String userWord, UUID gamePartyId) {
    ClientDto clientDto = null;
    String[] userCommand = userWord.split("\\s+");
    if (userCommand[0].equals("makemove")) {
      for (int i = 1; i < 5; i++) {
        if (!isNumeric(userCommand[i])) {
          return null;
        }
      }
      if (userCommand.length != 5) {
        return null;
      }
      clientDto =
          new MakeMoveDto(
              gamePartyId,
              charToInt(userCommand[1]),
              charToInt(userCommand[2]),
              charToInt(userCommand[3]),
              charToInt(userCommand[4]));
      return clientDto;
    }
    if (userCommand[0].equals("place")) {
      boolean inProcess = true;
      boolean general = false;
      if (!isNumeric(userCommand[1]) || !isNumeric(userCommand[2])) {
        return null;
      }
      if (!isUnitType(userCommand[3])) {
        return null;
      }
      if (userCommand.length == 4) {
        inProcess = true;
        general = false;
      }
      if (userCommand.length == 5) {
        if (!userCommand[4].equals("end")) {
          if (!userCommand[4].equals("general")) {
            return null;
          } else {
            general = true;
          }
        } else {
          inProcess = false;
        }
      }
      if (userCommand.length == 6) {
        if (!userCommand[4].equals("general")) {
          if (!userCommand[5].equals("end")) {
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
              charToInt(userCommand[1]),
              charToInt(userCommand[2]),
              getUnitType(userCommand[3]),
              inProcess,
              general);
    }
    if (userCommand[0].equals("endturn")) {
      if (userCommand.length != 1) {
        return null;
      }
      clientDto = new ChangePlayerDto(gamePartyId);
    }
    if (userCommand[0].equals("creategame")) {
      if (userCommand[1].equals("vs_human")) {
        clientDto = new CreateGamePartyDto(GameType.HUMAN_VS_HUMAN);
      } else if (userCommand[1].equals("vs_bot")) {
        clientDto = new CreateGamePartyDto(GameType.HUMAN_VS_BOT);
      } else {
        return null;
      }
    }
    if (userCommand[0].equals("joingame")) {
      if (userCommand.length != 2) {
        return null;
      }

      clientDto = new JoinGamePartyDto(UUID.fromString(userCommand[1]));
    }
    if(userCommand[0].equals("giveup"))
    {
      if (userCommand.length != 1){
        return null;
      }
      clientDto = new GiveUpDto(gamePartyId);
    }
    if(userCommand[0].equals("offer_giveup")){
      if (userCommand.length != 1){
        return null;
      }
      clientDto = new OfferGiveUpDto(gamePartyId);
    }
    return clientDto;
  }

  public static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
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
