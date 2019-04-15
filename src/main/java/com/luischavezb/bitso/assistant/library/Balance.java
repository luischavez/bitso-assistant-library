package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author luischavez
 */
public class Balance implements Serializable {

    static final long serialVersionUID = 1L;

    private String currency;
    private BigDecimal available;
    private BigDecimal locked;
    private BigDecimal total;

    public Balance(String currency, BigDecimal available, BigDecimal locked, BigDecimal total) {
        this.currency = currency;
        this.available = available;
        this.locked = locked;
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getLocked() {
        return locked;
    }

    public void setLocked(BigDecimal locked) {
        this.locked = locked;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return String.format("[%s] Available: %s Locked: %s Total: %s", currency, available, locked, total);
    }
}
