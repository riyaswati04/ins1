package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("payload")
public class InsightsGenerateURLPayload {
    private double apiVersion;

    private String vendorId;

    private String txnId;

    private String destination;

    private int loanAmount;

    private String loanType;

    private int loanDuration;

    private String acceptancePolicy;

    private String returnUrl;

    private String transactionCompleteCallbackUrl;

    private String yearMonthFrom;

    private String yearMonthTo;

    public InsightsGenerateURLPayload() {}

    public InsightsGenerateURLPayload(final double apiVersion, final String vendorId,
            final String txnId, final String destination, final int loanAmount,
            final int loanDuration, final String loanType,
            final String acceptancePolicy, final String returnUrl,
            final String transactionCompleteCallbackUrl, final String yearMonthFrom,
            final String yearMonthTo) {
        super();
        this.apiVersion = apiVersion;
        this.vendorId = vendorId;
        this.txnId = txnId;
        this.destination = destination;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
        this.loanType = loanType;
        this.acceptancePolicy = acceptancePolicy;
        this.returnUrl = returnUrl;
        this.transactionCompleteCallbackUrl = transactionCompleteCallbackUrl;
        this.yearMonthFrom = yearMonthFrom;
        this.yearMonthTo = yearMonthTo;
    }

    public String getAcceptancePolicy() {
        return acceptancePolicy;
    }

    public double getApiVersion() {
        return apiVersion;
    }

    public String getDestination() {
        return destination;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public int getLoanDuration() {
        return loanDuration;
    }

    public String getLoanType() {
        return loanType;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getTransactionCompleteCallbackUrl() {
        return transactionCompleteCallbackUrl;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getYearMonthFrom() {
        return yearMonthFrom;
    }

    public String getYearMonthTo() {
        return yearMonthTo;
    }

    public void setAcceptancePolicy(final String acceptancePolicy) {
        this.acceptancePolicy = acceptancePolicy;
    }

    public void setApiVersion(final double apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public void setLoanAmount(final int loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setLoanDuration(final int loanDuration) {
        this.loanDuration = loanDuration;
    }

    public void setLoanType(final String loanType) {
        this.loanType = loanType;
    }

    public void setReturnUrl(final String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void setTransactionCompleteCallbackUrl(final String transactionCallbackUrl) {
        transactionCompleteCallbackUrl = transactionCallbackUrl;
    }

    public void setTxnId(final String txnId) {
        this.txnId = txnId;
    }

    public void setVendorId(final String vendorId) {
        this.vendorId = vendorId;
    }

    public void setYearMonthFrom(final String yearMonthFrom) {
        this.yearMonthFrom = yearMonthFrom;
    }

    public void setYearMonthTo(final String yearMonthTo) {
        this.yearMonthTo = yearMonthTo;
    }
}
