package io.stockgeeks.ticker.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class StockQuote {
  private String symbol;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal tradeValue;
  private Exchange exchange;
  private Instant tradeTime;
}
