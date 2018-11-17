package com.codespair.ticker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProps {

  private List<String> hosts;
  private StockQuote stockQuote;

  @Data
  public static class StockQuote {
    private String topic;
  }
}
