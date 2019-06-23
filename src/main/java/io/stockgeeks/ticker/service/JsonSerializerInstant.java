package io.stockgeeks.ticker.service;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class JsonSerializerInstant<T> extends JsonSerializer<T> {

  public JsonSerializerInstant() {
    super();
    objectMapper.registerModule(new JavaTimeModule());
  }
}
