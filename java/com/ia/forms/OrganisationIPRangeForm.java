package com.ia.forms;

import com.ia.core.forms.Form;

public interface OrganisationIPRangeForm extends Form {

    Integer getId();

    Long getIpAddressRangeEnd();

    Long getIpAddressRangeStart();

    String getMode();

    Integer getOrganisationId();

}
