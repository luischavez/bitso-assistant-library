package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Withdrawal implements Serializable {

    static final long serialVersionUID = 1L;

    private String wid;
    private String status;
    private String currency;
    private String method;
    private BigDecimal amount;
    private String details;
    private Date createdAt;

    public Withdrawal(String wid, String status, String currency, String method, BigDecimal amount, String details, Date createdAt) {
        this.wid = wid;
        this.status = status;
        this.currency = currency;
        this.method = method;
        this.amount = amount;
        this.details = details;
        this.createdAt = createdAt;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
        Withdrawal that = (Withdrawal) object;
        return Objects.equals(wid, that.wid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getSimpleName(), wid);
    }
}
