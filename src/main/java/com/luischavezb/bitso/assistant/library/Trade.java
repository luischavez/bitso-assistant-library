package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Trade implements Serializable {

    static final long serialVersionUID = 1L;

    private Bitso.Book book;
    private BigDecimal major;
    private BigDecimal minor;
    private BigDecimal feesAmount;
    private String feesCurrency;
    private BigDecimal price;
    private String tid;
    private String oid;
    private String side;
    private Date createdAt;

    public Trade(Bitso.Book book, BigDecimal major, BigDecimal minor,
                 BigDecimal feesAmount, String feesCurrency, BigDecimal price,
                 String tid, String oid, String side,
                 Date createdAt) {
        this.book = book;
        this.major = major;
        this.minor = minor;
        this.feesAmount = feesAmount;
        this.feesCurrency = feesCurrency;
        this.price = price;
        this.tid = tid;
        this.oid = oid;
        this.side = side;
        this.createdAt = createdAt;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public BigDecimal getMajor() {
        return major;
    }

    public void setMajor(BigDecimal major) {
        this.major = major;
    }

    public BigDecimal getMinor() {
        return minor;
    }

    public void setMinor(BigDecimal minor) {
        this.minor = minor;
    }

    public BigDecimal getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(BigDecimal feesAmount) {
        this.feesAmount = feesAmount;
    }

    public String getFeesCurrency() {
        return feesCurrency;
    }

    public void setFeesCurrency(String feesCurrency) {
        this.feesCurrency = feesCurrency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Trade trade = (Trade) object;
        return Objects.equals(tid, trade.tid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getSimpleName(), tid);
    }
}
