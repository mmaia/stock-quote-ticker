package com.codespair.ticker.service.memorymap;

import com.codespair.ticker.config.GeneratorProps;
import com.codespair.ticker.config.KafkaProps;
import com.codespair.ticker.model.StockExchangeMaps;
import com.codespair.ticker.model.StockQuote;
import com.codespair.ticker.repository.kafka.StringJsonNodeClientProducer;
import com.codespair.ticker.service.TickGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

/**
 * This class on startup try to start generating random
 * stock quotes and produces them to kafka, unless disabled in the configuration.
 *
 * This class uses an in memory meta data map of the quotes loaded from the csv files and joins it with
 * randomly generated quotes.
 */
@Slf4j
@Service
@Profile("memory-map")
public class StockQuoteWithDetailsFromMemoryMapGenerator implements TickGenerator {

  private GeneratorProps generatorProps;
  private KafkaProps kafkaProps;
  private StockExchangeMaps stockExchangeMaps;
  private StringJsonNodeClientProducer quotesProducer;

  public StockQuoteWithDetailsFromMemoryMapGenerator(GeneratorProps generatorProps,
                                                     KafkaProps kafkaProps,
                                                     StockExchangeMaps stockExchangeMaps,
                                                     StringJsonNodeClientProducer quotesProducer) {
    this.generatorProps = generatorProps;
    this.kafkaProps = kafkaProps;
    this.stockExchangeMaps = stockExchangeMaps;
    this.quotesProducer = quotesProducer;
  }

  @Override
  @Async("producerTaskExecutor")
  public void startGenerator() {
    if (generatorProps.isEnabled()) {
      try {
        Thread.sleep(generatorProps.getStartDelayMilliseconds());
        log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
          generatorProps.getStartDelayMilliseconds(), generatorProps.getIntervalMilliseconds());
        while (true) {
          // in the next 2 calls a random quote is picked from the map and randomically generated values are added to the quote.
          StockQuote stockQuote = stockExchangeMaps.randomStockSymbolWithDetails();
          stockQuote = addRandomValues(stockQuote);
          quotesProducer.send(stockQuoteTopic(), stockQuote.getSymbol(), stockQuote); // send to kafka topic
          Thread.sleep(generatorProps.getIntervalMilliseconds());
        }
      } catch (InterruptedException e) {
        log.error("StockQuoteGenerator stopped producing quotes due to error: {}", e.getMessage());
      }
    }
  }

  @Override
  public String stockQuoteTopic() {
    return this.kafkaProps.getStockQuote().getTopic();
  }

  @PreDestroy
  public void closeProducer() {
    quotesProducer.close();
  }
}
