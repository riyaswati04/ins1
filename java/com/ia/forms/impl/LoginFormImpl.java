package com.ia.forms.impl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.constraints.NotBlank;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ia.core.annotations.ImplementationOf;
import com.ia.core.forms.impl.AbstractForm;
import com.ia.forms.LoginForm;

@ImplementationOf(form = LoginForm.class)
public class LoginFormImpl extends AbstractForm implements LoginForm {

    @NotBlank(message = "EMPTY_USER_NAME")
    private String username;

    @NotBlank(message = "EMPTY_PASSWORD")
    private String password;

    @Inject
    public LoginFormImpl(final ValidatorFactory validatorFactory,
            @Assisted final HttpServletRequest request) {
        super(validatorFactory, request);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUserName() {
        return username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setUserName(final String userName) {
        username = userName;
    }
}
