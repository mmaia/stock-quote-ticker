package com.codespair.ticker.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Configuration
public class StockExchangeMaps {

  private Map<String, Map<String, StockDetail>> exchanges;

  public StockExchangeMaps() {
    this.exchanges = new HashMap<>();
  }

  public void addExchangeMap(String key, Map<String, StockDetail> exchange) {
    exchanges.put(key, exchange);
  }

  public Map<String, StockDetail> getStockDetailsFromExchange(Exchange exchange) {
    return exchanges.get(exchange.name());
  }

  /**
   * Return number of instruments under that stpecific stock exchange.
   * @param exchange the exchange to get the number of trading instruments from
   * @return the number of instruments from the specified stock exchange
   */
  public int numStocks(Exchange exchange) {
    return exchanges.get(exchange.name()).size();
  }

  /**
   * @return stock quote picked randomically, enriched with meta data details.
   * @see StockQuote - with StockDetail.
   */
  public StockQuote randomStockSymbolWithDetails() {
    if (!valid()) return null;
    String exchange = Exchange.randomExchange();
    Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
    List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
    Random random = new Random();
    int whichInstrument = random.nextInt(symbols.size());
    return StockQuote.builder()
      .exchange(Exchange.valueOf(exchange))
      .symbol(symbols.get(whichInstrument))
      .stockDetail(stockDetailMap.get(symbols.get(whichInstrument)))
      .build();
  }

  /**
   * @return A simple stock quote with exchange and symbol only, no details.
   */
  public StockQuote randomStockSymbol() {
    if (!valid()) return null;
    String exchange = Exchange.randomExchange();
    Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
    List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
    Random random = new Random();
    int whichInstrument = random.nextInt(symbols.size());
    return StockQuote.builder()
      .exchange(Exchange.valueOf(exchange))
      .symbol(symbols.get(whichInstrument))
      .build();
  }

  private boolean valid() {
    return exchanges != null
      && exchanges.size() == Exchange.values().length;
  }
}
