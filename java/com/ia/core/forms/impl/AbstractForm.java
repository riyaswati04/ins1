package com.ia.core.forms.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import com.google.inject.assistedinject.Assisted;
import com.ia.core.forms.Form;

public abstract class AbstractForm implements Form {

    private final Map<String, String> errorMessages;

    private final List<String> nonFieldErrorMessages;

    private final String remoteAddress;

    private final HttpServletRequest request;

    private final String userAgent;

    private final ValidatorFactory validatorFactory;

    @Inject
    public AbstractForm(final ValidatorFactory validatorFactory,
            @Assisted final HttpServletRequest request) {
        super();

        this.request = request;
        this.validatorFactory = validatorFactory;

        remoteAddress = null != request ? request.getRemoteAddr() : null;
        userAgent = null != request ? request.getHeader("User-Agent") : null;
        errorMessages = newHashMap();
        nonFieldErrorMessages = newArrayList();

    }

    @Override
    public String getErrorMessage(final String fieldName) {
        return errorMessages.get(fieldName);
    }

    @Override
    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public String getFirstErrorMessage() {
        if (errorMessages != null) {
            for (final String key : errorMessages.keySet()) {
                return errorMessages.get(key);
            }
        }
        return null;
    }

    @Override
    public List<String> getNonFieldErrorMessages() {
        return nonFieldErrorMessages;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public void setErrorMessage(final String field, final String errorMessage) {
        if (isBlank(field)) {
            nonFieldErrorMessages.add(errorMessage);
        }
        else {
            errorMessages.put(field, errorMessage);
        }
    }

    private void setErrorMessages(final Set<ConstraintViolation<AbstractForm>> violations) {
        for (final ConstraintViolation<AbstractForm> cv : violations) {
            final Path propertyPath = cv.getPropertyPath();
            final String fieldName = propertyPath.toString();
            final String message = cv.getMessage();
            setErrorMessage(fieldName, message);
        }
    }

    @Override
    public void setNonFieldErrorMessage(final String errorMessage) {
        setErrorMessage(null, errorMessage);
    }

    @Override
    public boolean validate() {
        return validate(Default.class);
    }

    @Override
    public boolean validate(final Class<?> validationGroup) {
        final Validator validator = validatorFactory.getValidator();
        final Set<ConstraintViolation<AbstractForm>> violations =
                validate(validator, validationGroup);

        /* No constraint violations. Validation succeeded. */
        if (violations.size() == 0) {
            return true;
        }

        setErrorMessages(violations);
        return false;
    }

    private Set<ConstraintViolation<AbstractForm>> validate(final Validator validator,
            final Class<?> validationGroup) {
        return validator.validate(this, validationGroup);
    }

}
