package com.codespair.ticker.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper model to csv files with stock detail information per exchange, hence all fields mapped to strings to simplify.
 */

@Data
@Slf4j
public class StockDetail {
    private String symbol;
    private String name;
    private String lastSale;
    private String marketCap;
    private String ipoYear;
    private String sector;
    private String industry;
    private String summaryQuote;
}
