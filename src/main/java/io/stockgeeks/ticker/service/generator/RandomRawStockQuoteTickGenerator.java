package io.stockgeeks.ticker.service.generator;

import io.stockgeeks.ticker.config.TickGeneratorProps;
import io.stockgeeks.ticker.model.StockQuote;
import io.stockgeeks.ticker.model.TickTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class on startup try to start generating random
 * stockquotes unless disabled(default) in the configuration
 */
@Slf4j
@Service
public class RandomRawStockQuoteTickGenerator {

  private final TickGeneratorProps tickGeneratorProps;
  private final StockExchangeMeta stockExchangeMeta;

  private final TickProducer tickProducer;

  private ConcurrentHashMap<String, TickTracker> tickTrackerBySymbol = new ConcurrentHashMap<>();

  public RandomRawStockQuoteTickGenerator(TickGeneratorProps tickGeneratorProps,
                                          TickProducer tickProducer,
                                          StockExchangeMeta stockExchangeMeta) {
    this.tickGeneratorProps = tickGeneratorProps;
    this.tickProducer = tickProducer;
    this.stockExchangeMeta = stockExchangeMeta;

    log.info("Random generating raw stock ticks enabled: {}", tickGeneratorProps.isEnabled());
  }

  /**
   * Starts random generation of ticks which are produced to the raw-ticks topic, based on the static meta data
   * collected from the stock exchanges it randomly picks instruments to generate random value "ticks" for those
   * instruments.
   */
  @Async("tick-generator")
  public void startGenerator() throws InterruptedException {
    if (tickGeneratorProps.isEnabled()) {
      Thread.sleep(tickGeneratorProps.getStartDelayMilliseconds());
      log.info("Starting random quote generation in {} milliseconds, with interval: {} milliseconds between each quote",
        tickGeneratorProps.getStartDelayMilliseconds(), tickGeneratorProps.getIntervalMilliseconds());
      while (true) {
        StockQuote stockQuote = stockExchangeMeta.randomStockSymbol();
        stockQuote = enrich(stockQuote);
        tickProducer.send(stockQuote);
        Thread.sleep(tickGeneratorProps.getIntervalMilliseconds());
      }
    }
  }

  /**
   * Updates current values based on the previous value for that instrument
   * @param stockQuote to be updated
   * @return an updated StockQuote
   */
  StockQuote enrich(StockQuote stockQuote) {
    TickTracker tickTracker = processQuote(stockQuote);
    return buildUpdateQuote(stockQuote, tickTracker);
  }
  private StockQuote buildUpdateQuote(StockQuote stockQuote, TickTracker tickTracker) {
    stockQuote.setHigh(tickTracker.getHigh().setScale(3, RoundingMode.HALF_EVEN));
    stockQuote.setLow(tickTracker.getLow().setScale(3, RoundingMode.HALF_EVEN));
    stockQuote.setTradeValue(tickTracker.getCurrentPrice().setScale(3, RoundingMode.HALF_EVEN));
    stockQuote.setTradeTime(Instant.now());
    return stockQuote;
  }

  /**
   * Maintains track of ticks per symbol so we can base the price stats under a more realistic variation.
   *
   * @param stockQuote to be simulated.
   * @return tick data updated or initialized in cache.
   */
  private TickTracker processQuote(StockQuote stockQuote) {
    TickTracker tickTracker;
    if (tickTrackerBySymbol.get(stockQuote.getSymbol()) == null) {
      tickTracker = TickTracker.builder().symbol(stockQuote.getSymbol()).build();
      tickTracker = tickTracker.initializeValues();
      tickTrackerBySymbol.put(stockQuote.getSymbol(), tickTracker);
    } else {
      tickTracker = tickTrackerBySymbol.get(stockQuote.getSymbol());
      tickTracker = tickTracker.updateValues();
    }
    return tickTracker;
  }
}
