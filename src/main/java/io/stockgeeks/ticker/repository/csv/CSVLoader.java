package io.stockgeeks.ticker.repository.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import io.stockgeeks.ticker.model.Exchange;
import io.stockgeeks.ticker.model.StockDetail;
import io.stockgeeks.ticker.service.generator.StockExchangeMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that loads stock exchange csv files in memory maps that are then used as meta data to generate stock quote ticks.
 * CSV files are download from Nasdaq website, more specifically:
 * <a href="https://www.nasdaq.com/screening/companies-by-industry.aspx" target="_blank">Nasdaq Companies by Industry</a>
 */
@Slf4j
@Component("csvParser")
public class CSVLoader {

  private final StockExchangeMeta stockExchangeMeta;

  public CSVLoader(StockExchangeMeta stockExchangeMeta) {
    this.stockExchangeMeta = stockExchangeMeta;
  }

  /**
   * Uses CSVReader to read an parse the specified file and build a map where the stock symbol is the key and the
   * value is a StockDetail object with instrument attributes loaded from the csv files.
   *
   * @param filePath the filepath relative to the classpath where the csv is to be found, i.e - /static/AMEX.csv, the
   *                 expected csv column structure is a csv with attributes separated by comma ','  and with the structure:
   *                 "symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"
   * @returns a map where each key is a stock exchange symbol and each element is a StockDetail object filled with
   * data loaded from the CSVs.
   */
  Map<String, StockDetail> loadExchangeCSV(String filePath) {
    Map<String, StockDetail> result;
    CsvToBean<StockDetail> csv = new CsvToBean<>();
    try {
      InputStreamReader inputStreamReader = new InputStreamReader(new ClassPathResource(filePath).getInputStream());
      CSVReader reader = new CSVReader(inputStreamReader);
      csv.setCsvReader(reader);
      csv.setMappingStrategy(getStockDetailMappingStrategy());
      result = stockDetailsBySymbol(csv.parse());
      inputStreamReader.close();
    } catch (Exception e) {
      log.error("Failed to load csv file with exchange information because: {}", e.getMessage());
      throw new InvalidCSVPathException(e.getMessage());
    }
    return result;
  }

  public void loadCSVs() {
    Arrays.stream(Exchange.values()).forEach(exchange -> {
      stockExchangeMeta.addExchangeMap(exchange.name(), stockExchagesMap(exchange.name()));
      log.info("csv mapped: {}, number of stocks(instruments): {}", exchange.name(), stockExchangeMeta.numStocks(exchange));
    });
    log.info("All CSVs have been loaded");
  }

  private Map<String, StockDetail> stockExchagesMap(String exchange) {
    return loadExchangeCSV("/exchanges/" + exchange + ".csv");
  }

  private ColumnPositionMappingStrategy<StockDetail> getStockDetailMappingStrategy() {
    ColumnPositionMappingStrategy<StockDetail> loadStrategy = new ColumnPositionMappingStrategy<>();
    loadStrategy.setType(StockDetail.class);
    String[] columns = new String[]{"symbol", "name", "lastSale", "marketCap", "ipoYear", "sector", "industry", "summaryQuote"}; // the fields to bind do in your JavaBean
    loadStrategy.setColumnMapping(columns);
    return loadStrategy;
  }

  private Map<String, StockDetail> stockDetailsBySymbol(List<StockDetail> stockDetailList) {
    Map<String, StockDetail> stockDetailMap = new HashMap<>();
    stockDetailList.forEach(stockDetail -> stockDetailMap.put(stockDetail.getSymbol(), stockDetail));
    return stockDetailMap;
  }
}
