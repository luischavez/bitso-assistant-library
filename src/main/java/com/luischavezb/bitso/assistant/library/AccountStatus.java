package com.luischavezb.bitso.assistant.library;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountStatus implements Serializable {

    static final long serialVersionUID = 1L;

    private long clientId;
    private String firstName;
    private String lastName;
    private String status;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal dailyRemaining;
    private BigDecimal monthlyRemaining;
    private String cellphoneNumber;
    private String cellphoneNumberStored;
    private String emailStored;
    private String officialId;
    private String proofOfResidency;
    private String signedContract;
    private String originOfFunds;

    public AccountStatus(long clientId, String firstName, String lastName, String status,
                         BigDecimal dailyLimit, BigDecimal monthlyLimit, BigDecimal dailyRemaining, BigDecimal monthlyRemaining,
                         String cellphoneNumber, String cellphoneNumberStored, String emailStored,
                         String officialId, String proofOfResidency, String signedContract, String originOfFunds) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.dailyRemaining = dailyRemaining;
        this.monthlyRemaining = monthlyRemaining;
        this.cellphoneNumber = cellphoneNumber;
        this.cellphoneNumberStored = cellphoneNumberStored;
        this.emailStored = emailStored;
        this.officialId = officialId;
        this.proofOfResidency = proofOfResidency;
        this.signedContract = signedContract;
        this.originOfFunds = originOfFunds;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getDailyRemaining() {
        return dailyRemaining;
    }

    public void setDailyRemaining(BigDecimal dailyRemaining) {
        this.dailyRemaining = dailyRemaining;
    }

    public BigDecimal getMonthlyRemaining() {
        return monthlyRemaining;
    }

    public void setMonthlyRemaining(BigDecimal monthlyRemaining) {
        this.monthlyRemaining = monthlyRemaining;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getCellphoneNumberStored() {
        return cellphoneNumberStored;
    }

    public void setCellphoneNumberStored(String cellphoneNumberStored) {
        this.cellphoneNumberStored = cellphoneNumberStored;
    }

    public String getEmailStored() {
        return emailStored;
    }

    public void setEmailStored(String emailStored) {
        this.emailStored = emailStored;
    }

    public String getOfficialId() {
        return officialId;
    }

    public void setOfficialId(String officialId) {
        this.officialId = officialId;
    }

    public String getProofOfResidency() {
        return proofOfResidency;
    }

    public void setProofOfResidency(String proofOfResidency) {
        this.proofOfResidency = proofOfResidency;
    }

    public String getSignedContract() {
        return signedContract;
    }

    public void setSignedContract(String signedContract) {
        this.signedContract = signedContract;
    }

    public String getOriginOfFunds() {
        return originOfFunds;
    }

    public void setOriginOfFunds(String originOfFunds) {
        this.originOfFunds = originOfFunds;
    }

    @Override
    public String toString() {
        return String.format("%s %s Email: %s", firstName, lastName, emailStored);
    }
}
