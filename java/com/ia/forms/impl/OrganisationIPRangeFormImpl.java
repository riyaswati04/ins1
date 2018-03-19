package com.ia.forms.impl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.google.inject.assistedinject.Assisted;
import com.ia.core.annotations.ImplementationOf;
import com.ia.core.forms.impl.AbstractForm;
import com.ia.forms.OrganisationIPRangeForm;

@ImplementationOf(form = OrganisationIPRangeForm.class)
public class OrganisationIPRangeFormImpl extends AbstractForm implements OrganisationIPRangeForm {

    @NotNull(message = "EMPTY_ID")
    private Integer id;

    @NotNull(message = "EMPTY_ORG_ID")
    private Integer organisationId;

    @NotBlank(message = "EMPTY_MODE")
    private String mode;

    @NotNull(message = "EMPTY_IP_START")
    private Long ipAddressRangeStart;

    @NotNull(message = "EMPTY_IP_END")
    private Long ipAddressRangeEnd;

    @Inject
    public OrganisationIPRangeFormImpl(final ValidatorFactory validatorFactory,
            @Assisted final HttpServletRequest request) {
        super(validatorFactory, request);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Long getIpAddressRangeEnd() {
        return ipAddressRangeEnd;
    }

    @Override
    public Long getIpAddressRangeStart() {
        return ipAddressRangeStart;
    }

    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public Integer getOrganisationId() {
        return organisationId;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setIpAddressRangeEnd(final Long ipAddressRangeEnd) {
        this.ipAddressRangeEnd = ipAddressRangeEnd;
    }

    public void setIpAddressRangeStart(final Long ipAddressRangeStart) {
        this.ipAddressRangeStart = ipAddressRangeStart;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public void setOrganisationId(final Integer organisationId) {
        this.organisationId = organisationId;
    }

}
