package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Profile implements Serializable {

    static final long serialVersionUID = 1L;

    public enum Type {

        BUY, SELL
    }

    /**
     * Id del perfil.
     */
    private long id;

    /**
     * Tipo de perfil.
     */
    private Type type;

    /**
     * Libro.
     */
    private Bitso.Book book;

    /**
     * Indica si el perfil esta activado o no.
     */
    private boolean enabled;

    /**
     * Cantidad maxima a utilizar por operacion.
     */
    private BigDecimal maxAmount;

    /**
     * Precio.
     */
    private BigDecimal price;
    private BigDecimal currentPrice;

    /**
     * Indica si el precio se puede ajustar automaticamente a la baja.
     */
    private boolean automatic;

    /**
     * Tiempo en milisegundos de la ultima operacion.
     */
    private long lastOperationTime;

    /**
     * Tiempo en milisegundos a esperar para regresar al precio original si no se a realizado una compra
     * con el nuevo valor.
     */
    private long timeResetPrice;

    /**
     * Tiempo en milisegundos minimo de espera entre operaciones.
     */
    private long timeBetweenOperations;

    /**
     * Indica si se debe de tomar en cuenta la comisión.
     */
    private boolean useFee;

    /**
     * Indica si el perfil se debe de desactivar al realizar una operacion.
     */
    private boolean disable;

    public Profile(Type type, Bitso.Book book, boolean enabled, BigDecimal maxAmount, BigDecimal price,
                   boolean automatic, long timeResetPrice, long timeBetweenOperations,
                   boolean useFee, boolean disable) {
        id = System.currentTimeMillis() + System.currentTimeMillis();

        this.type = type;
        this.book = book;
        this.enabled = enabled;
        this.maxAmount = maxAmount;
        this.price = price;
        this.automatic = automatic;
        this.timeResetPrice = timeResetPrice;
        this.timeBetweenOperations = timeBetweenOperations;
        this.useFee = useFee;
        this.disable = disable;

        currentPrice = price;

        lastOperationTime = 0L;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Bitso.Book getBook() {
        return book;
    }

    public void setBook(Bitso.Book book) {
        this.book = book;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public long getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(long lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public long getTimeResetPrice() {
        return timeResetPrice;
    }

    public void setTimeResetPrice(long timeResetPrice) {
        this.timeResetPrice = timeResetPrice;
    }

    public long getTimeBetweenOperations() {
        return timeBetweenOperations;
    }

    public void setTimeBetweenOperations(long timeBetweenOperations) {
        this.timeBetweenOperations = timeBetweenOperations;
    }

    public boolean isUseFee() {
        return useFee;
    }

    public void setUseFee(boolean useFee) {
        this.useFee = useFee;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id == profile.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
