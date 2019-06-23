package io.stockgeeks.ticker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Tracks useful information about ticks like it's high, low, last tick price.
 */
@Builder
@Getter
public class TickTracker {

  @NonNull
  private final String symbol;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal currentPrice;

  private final static int UP_TO = 1_500; // initial price limit

  /**
   * Initialize this values when generating the ticks for the first time.
   * Randomically generates initial value for a stock and then it's lower and higher initial values based on the
   * current value that was initially generated.
   */
  public TickTracker initializeValues () {
    Random random = new Random();
    double randomStart = random.nextDouble();
    double currPrice = randomStart * UP_TO;
    currentPrice = new BigDecimal(currPrice);
    low = new BigDecimal(currPrice - randomStart);
    high = new BigDecimal(currPrice + randomStart);
    return this;
  }

  /**
   * Update it's values
   */
  public TickTracker updateValues() {
    Random random = new Random();
    // generates random value between -1 and 1
    double randomValue = random.nextDouble() * 2 -1;
    currentPrice = currentPrice.add(BigDecimal.valueOf(randomValue));
    if(currentPrice.doubleValue() < low.doubleValue()) {
      low = new BigDecimal(currentPrice.doubleValue()); // set new low
    } else if(currentPrice.doubleValue() > high.doubleValue()) {
      high = new BigDecimal(currentPrice.doubleValue()); // set new high
    }
    return this;
  }



}
