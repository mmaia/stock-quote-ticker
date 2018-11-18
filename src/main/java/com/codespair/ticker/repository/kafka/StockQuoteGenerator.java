package com.codespair.ticker.repository.kafka;

import com.codespair.ticker.config.GeneratorProps;
import com.codespair.ticker.config.KafkaProps;
import com.codespair.ticker.model.StockExchangeMaps;
import com.codespair.ticker.model.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Random;

/**
 * This class on startup try to start generating random
 * stock quotes and produces them to kafka, unless disabled in the configuration.
 *
 * This class uses an in memory meta data map of the quotes loaded from the csv files and joins it with
 * randomly generated quotes.
 */
@Slf4j
@Service
public class StockQuoteGenerator {

  private GeneratorProps generatorProps;
  private KafkaProps kafkaProps;
  private StockExchangeMaps stockExchangeMaps;
  private StringJsonNodeClientProducer quotesProducer;

  public StockQuoteGenerator(GeneratorProps generatorProps,
                             KafkaProps kafkaProps,
                             StockExchangeMaps stockExchangeMaps,
                             StringJsonNodeClientProducer quotesProducer) {
    this.generatorProps = generatorProps;
    this.kafkaProps = kafkaProps;
    this.stockExchangeMaps = stockExchangeMaps;
    this.quotesProducer = quotesProducer;
  }

  @Async("taskExecutor")
  public void startGenerator() {
    if (generatorProps.isEnabled()) {
      try {
        Thread.sleep(generatorProps.getStartDelayMilliseconds());
        log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
          generatorProps.getStartDelayMilliseconds(), generatorProps.getIntervalMilliseconds());
        while (true) {
          // in the next 2 calls a random quote is picked from the map and randomically generated values are added to the quote.
          StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
          stockQuote = enrich(stockQuote);

          quotesProducer.send(stockQuoteTopic(), stockQuote.getSymbol(), stockQuote); // send to kafka topic
          Thread.sleep(generatorProps.getIntervalMilliseconds());
        }
      } catch (InterruptedException e) {
        log.error("StockQuoteGenerator stopped producing quotes due to error: {}", e.getMessage());
      }
    }
  }

  private String stockQuoteTopic() {
    return this.kafkaProps.getStockQuote().getTopic();
  }

  // TODO - to better illustrate kafka usage move this
  //     routine to 2 topics one with stock details and another one with
  //     ticks and join them to generate final
  //     tick with stock details

  /**
   * Randomize values of high, low and lastTrade of quotes,
   *
   * @param stockQuote the quote to have some values randomized
   * @return StockQuote with high, low and lastTrade randomized.
   */
  public StockQuote enrich(StockQuote stockQuote) {
    Random random = new Random();
    int upTo = 1000;
    stockQuote.setHigh(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    stockQuote.setLow(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    stockQuote.setLastTrade(new BigDecimal(random.nextFloat() * upTo).setScale(3, BigDecimal.ROUND_CEILING));
    return stockQuote;
  }

  @PreDestroy
  public void closeProducer() {
    quotesProducer.close();
  }
}
