package com.codespair.ticker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "generator")
public class GeneratorProps {
  private boolean enabled;
  private int startDelayMilliseconds;
  private int intervalMilliseconds;
  private int numThreads;
}
