package io.stockgeeks.ticker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
@ConfigurationProperties(prefix = "generator.tick")
public class TickGeneratorProps {

  private boolean enabled;
  private int startDelayMilliseconds;
  private int intervalMilliseconds;
  private int numThreads;

  @Bean("tick-generator")
  public TaskExecutor tickGeneratorThreadPool() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(numThreads);
    executor.setMaxPoolSize(numThreads);
    executor.setThreadNamePrefix("tick-gen");
    executor.initialize();
    return executor;
  }
}
