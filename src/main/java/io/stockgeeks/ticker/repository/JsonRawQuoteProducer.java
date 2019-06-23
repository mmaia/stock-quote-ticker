package io.stockgeeks.ticker.repository;

import io.stockgeeks.ticker.model.StockQuote;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JsonRawQuoteProducer {

  private final KafkaTemplate<String, StockQuote> kafkaTemplate;

  public JsonRawQuoteProducer(KafkaTemplate<String, StockQuote> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(StockQuote stockQuote) {
    kafkaTemplate.send("raw-stock-quote-tick", stockQuote.getSymbol(), stockQuote);
  }
}
