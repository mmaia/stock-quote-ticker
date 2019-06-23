package io.stockgeeks.ticker.service;

import io.stockgeeks.ticker.model.StockQuote;
import io.stockgeeks.ticker.repository.JsonRawQuoteProducer;
import io.stockgeeks.ticker.service.generator.TickProducer;
import org.springframework.stereotype.Service;

@Service
public class JsonTickProducer implements TickProducer {

  private final JsonRawQuoteProducer jsonProducer;

  public JsonTickProducer(JsonRawQuoteProducer jsonRawQuoteProducer) {
    this.jsonProducer = jsonRawQuoteProducer;
  }

  @Override
  public void send(StockQuote stockQuote) {
    jsonProducer.send(stockQuote);
  }
}
