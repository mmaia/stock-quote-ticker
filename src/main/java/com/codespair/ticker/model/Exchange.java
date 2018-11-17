package com.codespair.ticker.model;

import java.util.Random;

/**
 * Enums with list of exchanges from which there are csv files to load the stock details from.
 */
public enum Exchange {
  AMEX,
  NYSE,
  NASDAQ;

  /**
   * Picks a random exchage and return it as String
   * @return - a random exchange.
   */
  public static String randomExchange() {
    Random random = new Random();
    int whichExchange = random.nextInt(values().length);
    return values()[whichExchange].name();
  }
}
