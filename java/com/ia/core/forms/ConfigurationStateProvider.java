package com.ia.core.forms;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import org.hibernate.validator.internal.engine.ConfigurationImpl;

import com.google.inject.Provider;

/**
 * The {@code javax.validation.spi.ConfigurationState} provider implementation.
 *
 */
@Singleton
public final class ConfigurationStateProvider implements Provider<ConfigurationState> {

    private final ConfigurationImpl configurationState;

    @Inject
    public ConfigurationStateProvider(final ValidationProvider<?> aProvider) {
        configurationState = new ConfigurationImpl(aProvider);
    }

    @Inject
    public void constraintValidatorFactory(
            final ConstraintValidatorFactory constraintValidatorFactory) {
        configurationState.constraintValidatorFactory(constraintValidatorFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationState get() {
        return configurationState;
    }

    @Inject
    public void messageInterpolator(final MessageInterpolator messageInterpolator) {
        configurationState.messageInterpolator(messageInterpolator);
    }

    @Inject
    public void traversableResolver(final TraversableResolver traversableResolver) {
        configurationState.traversableResolver(traversableResolver);
    }

}
