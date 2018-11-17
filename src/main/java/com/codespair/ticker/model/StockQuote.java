package com.codespair.ticker.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class StockQuote {
    private String symbol;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal lastTrade;
    private Exchange exchange;
    private StockDetail stockDetail;
}
