package com.codespair.ticker.service;

import com.codespair.ticker.config.GeneratorProps;
import com.codespair.ticker.config.KafkaProps;
import com.codespair.ticker.repository.csv.CsvParser;
import com.codespair.ticker.repository.csv.CsvToMemoryMapLoader;
import com.codespair.ticker.service.memorymap.StockQuoteClient;
import com.codespair.ticker.service.topicjoins.StockQuoteMetaDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@EnableAsync
@Component
public class LifeCycleManagement implements ApplicationListener<ApplicationReadyEvent> {

  private final CsvParser csvParser;
  private TickGenerator tick;
  private final GeneratorProps generatorProps;
  private final StockQuoteClient stockQuoteClient;
  private final StockQuoteMetaDetails stockQuoteMetaDetails;
  private final KafkaProps kafkaProps;

  public LifeCycleManagement(CsvToMemoryMapLoader csvToMemoryMapLoader,
                             TickGenerator tick,
                             GeneratorProps generatorProps,
                             StockQuoteClient stockQuoteClient,
                             StockQuoteMetaDetails stockQuoteMetaDetails,
                             KafkaProps kafkaProps) {
    this.csvParser = csvToMemoryMapLoader;
    this.tick = tick;
    this.generatorProps = generatorProps;
    this.stockQuoteClient = stockQuoteClient;
    this.stockQuoteMetaDetails = stockQuoteMetaDetails;
    this.kafkaProps = kafkaProps;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("starting the application engine, loading CSVs...");
    csvParser.loadCSVs();
    startQuoteProducer(generatorProps.getNumThreads());
    startQuoteConsumer(kafkaProps.getClient().getNumThreads());
    stockQuoteMetaDetails.produceStockQuoteMeta();
    log.info("Lifecycle initialization done!");
  }

  private void startQuoteProducer(int threads) {
    for (int i = 0; i < threads; i++) {
      try {
        tick.startGenerator();
        Thread.sleep(generatorProps.getIntervalMilliseconds() / threads);
      } catch (InterruptedException e) {
        log.error("Error initializing multi threaded producer, num threads: {}", threads);
      }
    }
  }

  private void startQuoteConsumer(int threads) {
    for (int i = 0; i < threads; i++) {
      stockQuoteClient.startConsumingStockQuoteTicks();
    }
  }

}
