package com.ia.forms.impl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.google.inject.assistedinject.Assisted;
import com.ia.core.annotations.ImplementationOf;
import com.ia.core.forms.impl.AbstractForm;
import com.ia.forms.OrganisationForm;

@ImplementationOf(form = OrganisationForm.class)
public class OrganisationFormImpl extends AbstractForm implements OrganisationForm {

    @NotBlank(message = "EMPTY_NAME")
    private String name;

    @NotBlank(message = "EMPTY_PASSPHRASE")
    private String passPhrase;

    @NotBlank(message = "EMPTY_MODE")
    private String modes;

    private boolean enabled;

    @NotNull(message = "EMPTY_ID")
    private Integer id;

    private String organisationKey;

    @Inject
    public OrganisationFormImpl(final ValidatorFactory validatorFactory,
            @Assisted final HttpServletRequest request) {
        super(validatorFactory, request);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getModes() {
        return modes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOrganisationKey() {
        return organisationKey;
    }

    @Override
    public String getPassPhrase() {
        return passPhrase;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setModes(final String modes) {
        this.modes = modes;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOrganisationKey(final String organisationKey) {
        this.organisationKey = organisationKey;
    }

    public void setPassPhrase(final String passPhrase) {
        this.passPhrase = passPhrase;
    }

}
