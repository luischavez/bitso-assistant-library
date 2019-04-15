package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;

public class FundingDestination implements Serializable {

    static final long serialVersionUID = 1L;

    private String currency;
    private String id;
    private String account;

    public FundingDestination(String currency, String id, String account) {
        this.currency = currency;
        this.id = id;
        this.account = account;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
