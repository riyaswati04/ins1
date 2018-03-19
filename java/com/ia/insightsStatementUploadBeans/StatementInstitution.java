package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("payload")
public class StatementInstitution {
    /*
     * <apiVersion>2.0</apiVersion> <destination>statement</destination>
     * <vendorId>vendorId</vendorId>
     */
    private double apiVersion;

    private String destination;

    private String vendorId;

    public StatementInstitution() {}

    public StatementInstitution(final double apiVersion, final String destination,
            final String vendorId) {
        super();
        this.apiVersion = apiVersion;
        this.destination = destination;
        this.vendorId = vendorId;
    }

    public double getApiVersion() {
        return apiVersion;
    }

    public String getDestination() {
        return destination;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setApiVersion(final double apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public void setVendorId(final String vendorId) {
        this.vendorId = vendorId;
    }
}
