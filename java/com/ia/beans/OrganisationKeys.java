package com.ia.beans;

public class OrganisationKeys {

    private boolean success;

    private String modulus;

    private String exponent;

    public OrganisationKeys(final boolean success) {
        super();
        this.success = success;
    }

    public String getExponent() {
        return exponent;
    }

    public String getModulus() {
        return modulus;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setExponent(final String exponent) {
        this.exponent = exponent;
    }

    public void setModulus(final String modulus) {
        this.modulus = modulus;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

}
