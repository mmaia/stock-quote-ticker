package com.codespair.ticker.service.topicjoins;

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

/**
 * Generates simple stock quotes and pushes to kafka. Generated quotes don't contain any extra information from stock details.
 * Produces to kafka using symbol as key and basic value information + exchange name.
 */
@Slf4j
@Service
@Profile("raw")
public class StockQuoteRawGenerator implements TickGenerator {

  private final KafkaProps kafkaProps;
  private final GeneratorProps generatorProps;
  private final StockExchangeMaps stockExchangeMaps;
  private final StringJsonNodeClientProducer quotesProducer;

  public StockQuoteRawGenerator(KafkaProps kafkaProps,
                                GeneratorProps generatorProps,
                                StockExchangeMaps stockExchangeMaps,
                                StringJsonNodeClientProducer quotesProducer) {
    this.kafkaProps = kafkaProps;
    this.generatorProps = generatorProps;
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
          StockQuote stockQuote = stockExchangeMaps.randomStockSymbol();
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
    return this.kafkaProps.getStockQuote().getRawTopic();
  }

}
