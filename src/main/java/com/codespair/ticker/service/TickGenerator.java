package com.codespair.ticker.service;

import com.codespair.ticker.model.StockQuote;

import java.math.BigDecimal;
import java.util.Random;

public interface TickGenerator {


  void startGenerator();
  String stockQuoteTopic();

  /**
   * Randomize values of high, low and lastTrade of quotes,
   *
   * @param stockQuote the quote to have some values randomized
   * @return StockQuote with high, low and lastTrade randomized.
   */
  default StockQuote addRandomValues(StockQuote stockQuote) {
    Random random = new Random();
    int upTo = 1000;
    stockQuote.setHigh(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    stockQuote.setLow(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    stockQuote.setLastTrade(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    return stockQuote;
  }
}
