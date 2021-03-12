package com.epam.training.task.threads;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

import static com.epam.training.task.threads.StockMarket.Operation.BUY;

public class Trader implements Runnable {

    private static final Random RANDOM = new Random();

    private int id;
    private BigDecimal usd;
    private BigDecimal byn;

    public Trader() {
    }

    public Trader(int id, BigDecimal usd, BigDecimal byn) {
        this.id = id;
        this.usd = usd;
        this.byn = byn;
    }

    public BigDecimal getUsd() {
        return usd;
    }

    public void setUsd(BigDecimal usd) {
        this.usd = usd;
    }

    public BigDecimal getByn() {
        return byn;
    }

    public void setByn(BigDecimal byn) {
        this.byn = byn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public void run() {
        StockMarket market = StockMarket.getInstance();
        for (int i = 0; i < 10; i++) {
            StockMarket.Operation operation = nextOperation();
            if (operation == BUY) {
                BigDecimal buyRatio = market.getBuyRatio();
                double buyPercent = 0.5;
                BigDecimal count = BigDecimal.valueOf(buyPercent).multiply(byn).divide(buyRatio, MathContext.DECIMAL128);
                market.doOperation(this, count, operation);
            } else {
                double sellPercent = 0.5;
                BigDecimal count = BigDecimal.valueOf(sellPercent).multiply(usd);
                market.doOperation(this, count, operation);
            }
        }
    }

    private static StockMarket.Operation nextOperation() {
        if (RANDOM.nextBoolean()) {
            return BUY;
        } else {
            return StockMarket.Operation.SELL;
        }
    }
}
