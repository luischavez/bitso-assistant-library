package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;

public class History implements Serializable {

    static final long serialVersionUID = 1L;

    private Bitso.Book book;
    private String date;
    private String dated;
    private BigDecimal value;
    private BigDecimal volume;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal close;
    private BigDecimal vwap;

    public History(Bitso.Book book, String date, String dated, BigDecimal value, BigDecimal volume, BigDecimal open, BigDecimal low, BigDecimal high, BigDecimal close, BigDecimal vwap) {
        this.book = book;
        this.date = date;
        this.dated = dated;
        this.value = value;
        this.volume = volume;
        this.open = open;
        this.low = low;
        this.high = high;
        this.close = close;
        this.vwap = vwap;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDated() {
        return dated;
    }

    public void setDated(String dated) {
        this.dated = dated;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
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

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVwap() {
        return vwap;
    }

    public void setVwap(BigDecimal vwap) {
        this.vwap = vwap;
    }
}
