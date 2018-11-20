package com.codespair.ticker.repository.kafka;

import com.codespair.ticker.config.KafkaProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class StockQuoteClient {
  final StringJsonNodeClientConsumer consumer;
  private final KafkaProps kafkaProps;

  public StockQuoteClient(StringJsonNodeClientConsumer consumer, KafkaProps kafkaProps) {
    this.consumer = consumer;
    this.kafkaProps = kafkaProps;
  }

  public void startConsumingStockQuoteTicks() {
    log.info("starting to consume ticks");
    consumer.startConsumer(kafkaProps.getClient().getGroupId(), Collections.singletonList(kafkaProps.getStockQuote().getTopic()));
  }
}

