package com.codespair.ticker.service;

import com.codespair.ticker.config.GeneratorProps;
import com.codespair.ticker.repository.csv.CsvToMemoryMapLoader;
import com.codespair.ticker.repository.kafka.StockQuoteGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@EnableAsync
@Component
public class LifeCycleManagement implements ApplicationListener<ApplicationReadyEvent> {

  private final CsvToMemoryMapLoader csvToMemoryMapLoader;
  private final StockQuoteGenerator stockQuoteGenerator;
  private final GeneratorProps generatorProps;

  public LifeCycleManagement(CsvToMemoryMapLoader csvToMemoryMapLoader, StockQuoteGenerator stockQuoteGenerator, GeneratorProps generatorProps) {
    this.csvToMemoryMapLoader = csvToMemoryMapLoader;
    this.stockQuoteGenerator = stockQuoteGenerator;
    this.generatorProps = generatorProps;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    log.info("starting the application engine, loading CSVs...");
    csvToMemoryMapLoader.loadCSVs();
    startQuoteProducer(generatorProps.getNumThreads());
    log.info("Lifecycle initialization done!");
  }

  private void startQuoteProducer(int threads) {
    for (int i = 0; i < threads; i++) {
      try {
        stockQuoteGenerator.startGenerator();
        Thread.sleep(generatorProps.getIntervalMilliseconds() / threads);
      } catch (InterruptedException e) {
        log.error("Error initializing multi threaded producer, num threads: {}", threads);
      }
    }
  }

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(generatorProps.getNumThreads());
    pool.setMaxPoolSize(generatorProps.getNumThreads());
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
  }
}
