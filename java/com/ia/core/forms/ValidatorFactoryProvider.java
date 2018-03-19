package com.ia.core.forms;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ConfigurationState;

import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;

import com.google.inject.Provider;

/**
 * Validator Factory Guice provider implementation.
 *
 */
@Singleton
public final class ValidatorFactoryProvider implements Provider<ValidatorFactory> {

    private final ValidatorFactoryImpl validatorFactory;

    @Inject
    public ValidatorFactoryProvider(final ConfigurationState configurationState) {
        validatorFactory = new ValidatorFactoryImpl(configurationState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorFactory get() {
        return validatorFactory;
    }

}
