package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author luischavez
 */
public class Ticker implements Serializable {

    static final long serialVersionUID = 1L;

    private Bitso.Book book;
    private BigDecimal volume;
    private BigDecimal vwap;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal ask;
    private BigDecimal bid;
    private BigDecimal last;
    private Date createdAt;

    public Ticker(Bitso.Book book,
                  BigDecimal volume, BigDecimal vwap,
                  BigDecimal low, BigDecimal high,
                  BigDecimal ask, BigDecimal bid, BigDecimal last,
                  Date createdAt) {
        this.book = book;
        this.volume = volume;
        this.vwap = vwap;
        this.low = low;
        this.high = high;
        this.ask = ask;
        this.bid = bid;
        this.last = last;
        this.createdAt = createdAt;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getVwap() {
        return vwap;
    }

    public void setVwap(BigDecimal vwap) {
        this.vwap = vwap;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] LOW: %s HIGH: %s ASK: %s BID: %s", book, low, high, ask, bid);
    }
}
