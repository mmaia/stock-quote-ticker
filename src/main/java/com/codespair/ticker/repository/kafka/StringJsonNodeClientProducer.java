package com.codespair.ticker.repository.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codespair.ticker.config.KafkaProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Properties;
import java.util.concurrent.Future;

@Slf4j
@Component
public class StringJsonNodeClientProducer implements AutoCloseable {

  private final KafkaProps config;
  private Producer<String, JsonNode> kafkaProducer;

  public StringJsonNodeClientProducer(KafkaProps kafkaProps) {
    this.config = kafkaProps;
    kafkaProducer = new KafkaProducer<>(kafkaClientProperties());
  }

  Future<RecordMetadata> send(String topic, String key, Object instance) {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.convertValue(instance, JsonNode.class);
    return kafkaProducer.send(new ProducerRecord<>(topic, key,
      jsonNode));
  }

  @PreDestroy
  public void close() {
    kafkaProducer.close();
  }

  private Properties kafkaClientProperties() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getHosts());
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return properties;
  }
}

