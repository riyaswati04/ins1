package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("payload")
public class RetrievePayload {
    private double apiVersion;

    private String perfiosTransactionId;

    private String reportType;

    private String txnId;

    private String vendorId;

    public RetrievePayload() {}

    public RetrievePayload(final double apiVersion, final String perfiosTransactionId,
            final String reportType, final String txnId, final String vendorId) {
        super();
        this.apiVersion = apiVersion;
        this.perfiosTransactionId = perfiosTransactionId;
        this.reportType = reportType;
        this.txnId = txnId;
        this.vendorId = vendorId;
    }

    public double getApiVersion() {
        return apiVersion;
    }

    public String getPerfiosTransactionId() {
        return perfiosTransactionId;
    }

    public String getReportType() {
        return reportType;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setApiVersion(final double apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setPerfiosTransactionId(final String perfiosTransasctionId) {
        perfiosTransactionId = perfiosTransasctionId;
    }

    public void setReportType(final String reportType) {
        this.reportType = reportType;
    }

    public void setTxnId(final String txnId) {
        this.txnId = txnId;
    }

    public void setVendorId(final String vendorId) {
        this.vendorId = vendorId;
    }

}
