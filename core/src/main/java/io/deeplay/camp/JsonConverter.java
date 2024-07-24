package io.deeplay.camp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String serialize(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  public static <T> T deserialize(String jsonString, Class<T> clazz)
      throws JsonProcessingException {
    return objectMapper.readValue(jsonString, clazz);
  }
}
