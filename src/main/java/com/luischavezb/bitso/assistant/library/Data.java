package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Data implements Serializable {

    static final long serialVersionUID = 1L;

    /**
     * Listado de comisiones.
     */
    private List<Fee> fees;

    /**
     * Listado de balances en la cuenta.
     */
    private List<Balance> balances;

    /**
     * Listado de precios en el mercado.
     */
    private List<Ticker> tickers;

    /**
     * Perfiles de compra/venta.
     */
    private List<Profile> profiles;

    public Data() {
        this.fees = new CopyOnWriteArrayList<>();
        this.balances = new CopyOnWriteArrayList<>();
        this.tickers = new CopyOnWriteArrayList<>();
        this.profiles = new CopyOnWriteArrayList<>();
    }

    public List<Fee> getFees() {
        return fees;
    }

    public void setFees(List<Fee> fees) {
        this.fees = fees;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public List<Ticker> getTickers() {
        return tickers;
    }

    public void setTickers(List<Ticker> tickers) {
        this.tickers = tickers;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Ticker tickerOfBook(Bitso.Book book) {
        for (Ticker ticker : tickers) {
            if (ticker.getBook().equals(book)) {
                return ticker;
            }
        }

        return null;
    }

    public Balance[] balancesOfBook(Bitso.Book book) {
        Balance majorBalance = null;
        Balance minorBalance = null;

        String[] majorMinor = book.name().toLowerCase().split("_");
        for (Balance balance : balances) {
            if (balance.getCurrency().equals(majorMinor[0])) {
                majorBalance = balance;
            }

            if (balance.getCurrency().equals(majorMinor[1])) {
                minorBalance = balance;
            }
        }

        return new Balance[]{majorBalance, minorBalance};
    }

    public Fee feeOfBook(Bitso.Book book) {
        for (Fee fee : fees) {
            if (fee.getBook().equals(book)) {
                return fee;
            }
        }

        return null;
    }
}
