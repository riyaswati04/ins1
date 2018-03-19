package com.ia.core.forms;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import com.google.inject.Injector;

/**
 * {@code javax.validation.ConstraintValidatorFactory} implementation that relies on Google Guice.
 *
 */
@Singleton
public final class GuiceAwareConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final Injector injector;

    @Inject
    public GuiceAwareConstraintValidatorFactory(final Injector injector) {
        this.injector = injector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        return injector.getInstance(key);
    }

}
