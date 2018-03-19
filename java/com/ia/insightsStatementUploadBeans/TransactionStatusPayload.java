package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("payload")
public class TransactionStatusPayload {
    /*
     * <apiVersion>2.0</apiVersion> <txnId>PQ1342687YTX</txnId> <vendorId>vendorId</vendorId>
     */
    private double apiVersion;

    private String txnId;

    private String vendorId;

    public TransactionStatusPayload() {}

    public TransactionStatusPayload(final double apiVersion, final String txnId,
            final String vendorId) {
        super();
        this.apiVersion = apiVersion;
        this.txnId = txnId;
        this.vendorId = vendorId;
    }

    public double getApiVersion() {
        return apiVersion;
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

    public void setTxnId(final String txnId) {
        this.txnId = txnId;
    }

    public void setVendorId(final String vendorId) {
        this.vendorId = vendorId;
    }
}
