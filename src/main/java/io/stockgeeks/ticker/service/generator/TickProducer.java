package io.stockgeeks.ticker.service.generator;

import io.stockgeeks.ticker.model.StockQuote;

public interface TickProducer {
  void send(StockQuote stockQuote);
}
