package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Funding implements Serializable {

    static final long serialVersionUID = 1L;

    private String fid;
    private String status;
    private String currency;
    private String method;
    private BigDecimal amount;
    private String details;
    private Date createdAt;

    public Funding(String fid, String status, String currency, String method, BigDecimal amount, String details, Date createdAt) {
        this.fid = fid;
        this.status = status;
        this.currency = currency;
        this.method = method;
        this.amount = amount;
        this.details = details;
        this.createdAt = createdAt;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
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
        Funding funding = (Funding) object;
        return Objects.equals(fid, funding.fid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getSimpleName(), fid);
    }
}
