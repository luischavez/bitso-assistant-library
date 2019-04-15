package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author luischavez
 */
public class Fee implements Serializable {

    static final long serialVersionUID = 1L;

    private Bitso.Book book;
    private BigDecimal decimal;
    private BigDecimal percent;

    public Fee(Bitso.Book book, BigDecimal decimal, BigDecimal percent) {
        this.book = book;
        this.decimal = decimal;
        this.percent = percent;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return String.format("[%s] decimal: %s percent: %s", book, decimal, percent);
    }
}
