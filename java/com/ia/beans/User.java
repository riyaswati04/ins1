package com.ia.beans;

public class User implements OrganisationMetadata {
    private String emailId;

    private int organisationId;

    private int userId;

    public String getEmailId() {
        return emailId;
    }

    @Override
    public int getOrganisationId() {
        return organisationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    public void setOrganisationId(final int organisationId) {
        this.organisationId = organisationId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }
}
