package com.epam.training.task.threads;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class StockMarket {

    private static volatile StockMarket INSTANCE;

    public enum Operation {
        BUY, SELL
    }

    private static final Logger LOGGER = Logger.getRootLogger();

    private BigDecimal buyRatio = new BigDecimal("2.0");
    private BigDecimal sellRatio = new BigDecimal("1.9");
    private static final BigDecimal deltaRatio = new BigDecimal("1000000.0");

    private final ReentrantLock lock = new ReentrantLock();

    private StockMarket() {}

    public static StockMarket getInstance() {
        StockMarket localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (StockMarket.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    localInstance = new StockMarket();
                    INSTANCE = localInstance;
                }
            }
        }
        return localInstance;
    }

    public BigDecimal getBuyRatio() {
        return buyRatio;
    }

    public BigDecimal getSellRatio() {
        return sellRatio;
    }

    public void doOperation(Trader trader, BigDecimal count, Operation type) {
        lock.lock();
        try {
            switch (type) {
                case BUY:
                    buyUsd(trader, count);
                    break;
                case SELL:
                    sellUsd(trader, count);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } finally {
            lock.unlock();
        }
    }

    private void buyUsd(Trader trader, BigDecimal count) {
        BigDecimal total = buyRatio.multiply(count);
        BigDecimal startSellRatio = sellRatio;
        BigDecimal startBuyRatio = buyRatio;
        BigDecimal startByn = trader.getByn();
        BigDecimal startUsd = trader.getUsd();

        if (trader.getByn().compareTo(total) >= 0) {

            BigDecimal newBynValue = trader.getByn().subtract(total);
            BigDecimal newUsdValue = trader.getUsd().add(count);
            trader.setByn(newBynValue);
            trader.setUsd(newUsdValue);

            BigDecimal delta = count.divide(deltaRatio, MathContext.DECIMAL128).min(new BigDecimal("0.1"));
            buyRatio = buyRatio.add(delta);
            sellRatio = sellRatio.add(delta);

            System.out.println("Trader #" + trader.getId() + " has bought " + format(count) + " USD with buy ratio " + format(startBuyRatio));
            System.out.println("Transaction cost: " + format(total));
            System.out.println("Total balance - USD: " + format(startUsd) + " -> " + format(trader.getUsd()) + " BYN: " + format(startByn) + " -> " + format(trader.getByn()));
        } else {
            System.out.println("Trader #" + trader.getId() + " couldn't buy " + format(count) + " USD with buy ratio " + format(buyRatio) + ". Not enough byn. ");
            System.out.println("Transaction cost: " + format(total));
            System.out.println("Total balance - USD: " + format(trader.getUsd()) + " BYN: " + format(trader.getByn()));
        }
        System.out.println("USD покупка: " + format(startBuyRatio) + " -> " + format(buyRatio));
        System.out.println("USD продажа: " + format(startSellRatio) + " -> " + format(sellRatio));
        System.out.println("--------------");
    }

    private String format(BigDecimal bigDecimal) {
        return bigDecimal.setScale(4, BigDecimal.ROUND_DOWN).toString();
    }

    private void sellUsd(Trader trader, BigDecimal count) {
        BigDecimal startSellRatio = sellRatio;
        BigDecimal startBuyRatio = buyRatio;
        BigDecimal startByn = trader.getByn();
        BigDecimal startUsd = trader.getUsd();

        if (trader.getUsd().compareTo(count) >= 0) {

            BigDecimal total = count.multiply(sellRatio);

            BigDecimal newBynValue = trader.getByn().add(total);
            BigDecimal newUsdValue = trader.getUsd().subtract(count);
            trader.setByn(newBynValue);
            trader.setUsd(newUsdValue);

            BigDecimal delta = count.divide(deltaRatio, MathContext.DECIMAL128).min(new BigDecimal("0.1"));;
            buyRatio = buyRatio.subtract(delta).max(new BigDecimal("0.11"));
            sellRatio = sellRatio.subtract(delta).max(new BigDecimal("0.01"));

            System.out.println("Trader #" + trader.getId() + " has sold " + format(count) + " USD with sell ratio " + format(startSellRatio) + ". ");
            System.out.println("Total balance - USD: " + format(startUsd) + " -> " + format(trader.getUsd()) + " BYN: " + format(startByn) + " -> " + format(trader.getByn()));
        } else {
            System.out.println("Trader #" + trader.getId() + " couldn't sell " + format(count) + " USD with sell ratio " + format(sellRatio) + ". Not enough usd.");
            System.out.println("Total balance - USD: " + format(trader.getUsd()) + " BYN: " + format(trader.getByn()));
        }
        System.out.println("USD покупка: " + format(startBuyRatio) + " -> " + format(buyRatio));
        System.out.println("USD продажа: " + format(startSellRatio) + " -> " + format(sellRatio));
        System.out.println("-------------------");
    }



}
