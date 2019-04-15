package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author luischavez
 */
public class Order implements Serializable {

    static final long serialVersionUID = -5549714687077125439L;

    private Bitso.Book book;
    private BigDecimal originalAmount;
    private BigDecimal unfilledAmount;
    private BigDecimal originalValue;
    private BigDecimal price;
    private String oid;
    private String side;
    private String status;
    private String type;
    private Date createdAt;
    private Date updatedAt;

    public Order(Bitso.Book book,
                 BigDecimal originalAmount, BigDecimal unfilledAmount,
                 BigDecimal originalValue, BigDecimal price,
                 String oid, String side, String status, String type,
                 Date createdAt, Date updatedAt) {
        this.book = book;
        this.originalAmount = originalAmount;
        this.unfilledAmount = unfilledAmount;
        this.originalValue = originalValue;
        this.price = price;
        this.oid = oid;
        this.side = side;
        this.status = status;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Order(Bitso.Book book, String oid) {
        this(book, null, null, null, null, oid, null, null, null, null, null);
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getUnfilledAmount() {
        return unfilledAmount;
    }

    public void setUnfilledAmount(BigDecimal unfilledAmount) {
        this.unfilledAmount = unfilledAmount;
    }

    public BigDecimal getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(BigDecimal originalValue) {
        this.originalValue = originalValue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;

        if (obj instanceof String && oid.equals(obj.toString())) return true;

        if (!(obj instanceof Order)) return false;

        return oid.equals(((Order) obj).oid);
    }
}
