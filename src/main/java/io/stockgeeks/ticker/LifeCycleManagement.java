package io.stockgeeks.ticker;

import io.stockgeeks.ticker.config.TickGeneratorProps;
import io.stockgeeks.ticker.repository.csv.CSVLoader;
import io.stockgeeks.ticker.service.generator.RandomRawStockQuoteTickGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;

@Slf4j
@EnableKafka
@EnableConfigurationProperties(TickGeneratorProps.class)
@Service
public class LifeCycleManagement implements ApplicationListener<ApplicationReadyEvent> {

  private final CSVLoader csvLoader;
  private final TickGeneratorProps tickGeneratorProps;
  private final RandomRawStockQuoteTickGenerator randomRawStockQuoteTickGenerator;

  public LifeCycleManagement(CSVLoader csvLoader,
                             TickGeneratorProps tickGeneratorProps,
                             RandomRawStockQuoteTickGenerator randomRawStockQuoteTickGenerator) {
    this.csvLoader = csvLoader;
    this.tickGeneratorProps = tickGeneratorProps;
    this.randomRawStockQuoteTickGenerator = randomRawStockQuoteTickGenerator;
  }

    @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    csvLoader.loadCSVs();
      try {
        randomRawStockQuoteTickGenerator.startGenerator();
      } catch (InterruptedException e) {
        log.error("Error initializing generator");
        e.printStackTrace();
      }
    }
}
