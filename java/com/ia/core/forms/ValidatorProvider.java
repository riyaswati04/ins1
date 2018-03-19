package com.ia.core.forms;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.Provider;

@Singleton
public final class ValidatorProvider implements Provider<Validator> {

    /**
     * The Validator reference.
     */
    private final Validator validator;

    /**
     * Build a new ValidatorProvider by ValidatorFactory.
     *
     * @param validatorFactory the ValidatorFactory reference.
     */
    @Inject
    public ValidatorProvider(final ValidatorFactory validatorFactory) {
        validator = validatorFactory.getValidator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator get() {
        return validator;
    }

}
