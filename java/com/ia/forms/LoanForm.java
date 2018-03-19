package com.ia.forms;

import com.ia.core.forms.Form;

public interface LoanForm extends Form {

    String getEmailId();

    int getLoanAmount();

    int getLoanDuration();

    String getLoanType();

    String getMobileNumber();

    String getName();

    String getOrganisation();

}
