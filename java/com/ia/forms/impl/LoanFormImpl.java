package com.ia.forms.impl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ia.core.annotations.ImplementationOf;
import com.ia.core.forms.impl.AbstractForm;
import com.ia.forms.LoanForm;
import com.ia.forms.LoginForm;

@ImplementationOf(form = LoginForm.class)
public class LoanFormImpl extends AbstractForm implements LoanForm {

    @NotBlank(message = "EMPTY_EMAIL_ID")
    private String emailId;

    @NotBlank(message = "EMPTY_NAME")
    private String name;

    @NotBlank(message = "EMPTY_MOBILE_NUMBER")
    private String mobileNumber;

    private String organisation;

    @NotBlank(message = "EMPTY_LOAN_TYPE")
    private String loanType;

    @Min(value = 1, message = "INVALID_LOAN_AMOUNT")
    private int loanAmount;

    @Min(value = 1, message = "INVALID_LOAN_DURATION")
    private int loanDuration;

    @Inject
    public LoanFormImpl(final ValidatorFactory validatorFactory,
            @Assisted final HttpServletRequest request) {
        super(validatorFactory, request);
    }

    @Override
    public String getEmailId() {
        return emailId;
    }

    @Override
    public int getLoanAmount() {
        return loanAmount;
    }

    @Override
    public int getLoanDuration() {
        return loanDuration;
    }

    @Override
    public String getLoanType() {
        return loanType;
    }

    @Override
    public String getMobileNumber() {
        return mobileNumber;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOrganisation() {
        return organisation;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
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

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOrganisation(final String organisation) {
        this.organisation = organisation;
    }

   
}
