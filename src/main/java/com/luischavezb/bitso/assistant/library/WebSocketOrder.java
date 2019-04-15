package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class WebSocketOrder implements Serializable {

    static final long serialVersionUID = 1L;

    private Bitso.Book book;
    private String type;
    private String oid;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal value;
    private long timestamp;

    public WebSocketOrder(Bitso.Book book, String type, String oid, BigDecimal rate, BigDecimal amount, BigDecimal value, long timestamp) {
        this.book = book;
        this.type = type;
        this.oid = oid;
        this.rate = rate;
        this.amount = amount;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketOrder order = (WebSocketOrder) o;
        return Objects.equals(oid, order.oid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oid);
    }
}
