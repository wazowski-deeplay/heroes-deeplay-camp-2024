package io.deeplay.camp.core.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String serialize(Object object) throws JsonProcessingException {
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    return objectMapper.writeValueAsString(object);
  }

  public static <T> T deserialize(String jsonString, Class<T> clazz)
      throws JsonProcessingException {
    return objectMapper.readValue(jsonString, clazz);
  }
}
