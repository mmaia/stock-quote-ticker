package com.codespair.ticker.service.topicjoins;

import com.codespair.ticker.model.Exchange;
import com.codespair.ticker.model.StockDetail;
import com.codespair.ticker.model.StockExchangeMaps;
import com.codespair.ticker.repository.kafka.StringJsonNodeClientProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class StockQuoteMetaDetails {
  private StockExchangeMaps stockExchangeMaps;
  private StringJsonNodeClientProducer quotesProducer;

  public StockQuoteMetaDetails(StockExchangeMaps stockExchangeMaps,
                               StringJsonNodeClientProducer quotesProducer) {

    this.stockExchangeMaps = stockExchangeMaps;
    this.quotesProducer = quotesProducer;
  }

  /**
   * For now we just produce all meta information collected from the csv files to a topic in kafka
   */
  @Async("singleThreadedProducerTaskExecutor")
  public void produceStockQuoteMeta() {
    Exchange[] exchanges = Exchange.values();
    for (int i = 0; i < exchanges.length; i++) {
      final Exchange exchange = exchanges[i];
      Map<String, StockDetail> stockDetailsFromExchange = stockExchangeMaps.getStockDetailsFromExchange(exchange);
      stockDetailsFromExchange.forEach((symbol, stockDetail) -> {
        stockDetail.setExchange(exchange);
        quotesProducer.send(STOCK_QUOTE_META_DETAILS_TOPIC, symbol, stockDetail);
      });
    }
    log.info("Stock Quote Meta Details finished to be produced to kafka topic: {}", STOCK_QUOTE_META_DETAILS_TOPIC);
  }
  final static String STOCK_QUOTE_META_DETAILS_TOPIC = "stock-quote-meta-details";
}
