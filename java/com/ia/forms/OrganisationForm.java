package com.ia.forms;

import com.ia.core.forms.Form;

public interface OrganisationForm extends Form {

    Integer getId();

    String getModes();

    String getName();

    String getOrganisationKey();

    String getPassPhrase();

    boolean isEnabled();

}
