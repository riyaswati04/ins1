package com.ia.beans;

import java.sql.Timestamp;
import java.util.Date;

public class UserData {

    private int id;

    private String transactionId;

    private String perfiosTranscationId;

    private int userId;

    private String genratedLink;

    private Date expiryDate;

    private String status;

    private String insightsStatus;

    private String message;

    private String reportLocation;

    private boolean isDeleted;

    private Timestamp updated;

    private Timestamp created;

    private String formDataJson;

    public Timestamp getCreated() {
        return created;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getGenratedLink() {
        return genratedLink;
    }

    public int getId() {
        return id;
    }

    public String getInsightsStatus() {
        return insightsStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getPerfiosTranscationId() {
        return perfiosTranscationId;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public String getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson) {
        this.formDataJson = formDataJson;
    }

    public void setCreated(final Timestamp created) {
        this.created = created;
    }

    public void setDeleted(final boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setGenratedLink(final String genratedLink) {
        this.genratedLink = genratedLink;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setInsightsStatus(final String insightsStatus) {
        this.insightsStatus = insightsStatus;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setPerfiosTranscationId(final String perfiosTranscationId) {
        this.perfiosTranscationId = perfiosTranscationId;
    }

    public void setReportLocation(final String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public void setUpdated(final Timestamp updated) {
        this.updated = updated;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

}
