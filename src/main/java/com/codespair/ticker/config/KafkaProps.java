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
  private KafkaClient client;

  @Data
  public static class StockQuote {
    private String topic;
    private String rawTopic;
  }

  @Data
  public static class KafkaClient {
    private String groupId;
    private int numThreads;
  }

}
