package io.stockgeeks.ticker.service.generator;

import io.stockgeeks.ticker.model.Exchange;
import io.stockgeeks.ticker.model.StockDetail;
import io.stockgeeks.ticker.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class StockExchangeMeta {

  private Map<String, Map<String, StockDetail>> exchanges;

  public StockExchangeMeta() {
    this.exchanges = new HashMap<>();
  }

  public void addExchangeMap(String key, Map<String, StockDetail> exchange) {
    exchanges.put(key, exchange);
  }

  public Map<String, StockDetail> getStockDetailsFromExchange(Exchange exchange) {
    return exchanges.get(exchange.name());
  }

  /**
   * Return number of instruments under that specific stock exchange.
   * @param exchange the exchange to get the number of trading instruments from
   * @return the number of instruments from the specified stock exchange
   */
  public int numStocks(Exchange exchange) {
    return exchanges.get(exchange.name()).size();
  }

  /**
   * @return stock quote picked randomically.
   * @see StockQuote
   */
  public StockQuote randomStockSymbol() {
    if (!valid()) return null;
    String exchange = Exchange.randomExchange();
    Map<String, StockDetail> stockDetailMap = exchanges.get(exchange);
    List<String> symbols = new ArrayList<>(stockDetailMap.keySet());
    Random random = new Random();
    int whichInstrument = random.nextInt(symbols.size());
    StockQuote stockQuote = new StockQuote();
    stockQuote.setExchange(Exchange.valueOf(exchange));
    stockQuote.setSymbol(symbols.get(whichInstrument));
    return stockQuote;
  }

  private boolean valid() {
    return exchanges != null
      && exchanges.size() == Exchange.values().length;
  }
}
