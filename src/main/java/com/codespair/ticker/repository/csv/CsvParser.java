package com.codespair.ticker.repository.csv;

import com.codespair.ticker.model.StockDetail;
import com.opencsv.bean.ColumnPositionMappingStrategy;

public interface CsvParser {

  void loadCSVs();

  default ColumnPositionMappingStrategy<StockDetail> getStockDetailMappingStrategy() {
    ColumnPositionMappingStrategy<StockDetail> loadStrategy = new ColumnPositionMappingStrategy<>();
    loadStrategy.setType(StockDetail.class);
    String[] columns = new String[]{"symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"}; // the fields to bind do in your JavaBean
    loadStrategy.setColumnMapping(columns);
    return loadStrategy;
  }
}
