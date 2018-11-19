package com.codespair.ticker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolExecutor {

  private final GeneratorProps generatorProps;
  private final KafkaProps kafkaProps;

  public ThreadPoolExecutor(GeneratorProps generatorProps, KafkaProps kafkaProps) {
    this.generatorProps = generatorProps;
    this.kafkaProps = kafkaProps;
  }

  @Bean
  public ThreadPoolTaskExecutor clientTaskExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(kafkaProps.getClient().getNumThreads());
    pool.setMaxPoolSize(kafkaProps.getClient().getNumThreads());
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
  }

  @Bean
  public ThreadPoolTaskExecutor producerTaskExecutor() {
    ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    pool.setCorePoolSize(generatorProps.getNumThreads());
    pool.setMaxPoolSize(generatorProps.getNumThreads());
    pool.setWaitForTasksToCompleteOnShutdown(true);
    return pool;
  }

}
