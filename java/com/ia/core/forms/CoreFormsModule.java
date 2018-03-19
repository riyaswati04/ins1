package com.ia.core.forms;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.engine.resolver.DefaultTraversableResolver;
import org.hibernate.validator.messageinterpolation.ValueFormatterMessageInterpolator;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.ia.core.forms.impl.FormBuilderImpl;
import com.ia.core.util.HttpBeanUtil;

public final class CoreFormsModule extends AbstractModule {

    @Override
    protected void configure() {
        /* For creating and validating (HTTP)Form objects. */

        /* Form Validation */
        /* Bindings for Hibernate Validate. */
        bind(MessageInterpolator.class).to(ValueFormatterMessageInterpolator.class)
                .in(Singleton.class);

        bind(TraversableResolver.class).to(DefaultTraversableResolver.class).in(Singleton.class);

        bind(ConstraintValidatorFactory.class).to(GuiceAwareConstraintValidatorFactory.class)
                .in(Singleton.class);

        bind(new TypeLiteral<ValidationProvider<?>>() {}).to(HibernateValidator.class)
                .in(Singleton.class);

        bind(ConfigurationState.class).toProvider(ConfigurationStateProvider.class)
                .in(Singleton.class);

        bind(ValidatorFactory.class).toProvider(ValidatorFactoryProvider.class).in(Singleton.class);

        bind(Validator.class).toProvider(ValidatorProvider.class);

        /* Form Construction. */
        /* This class builds Form instances from HttpServletRequest instances. */
        bind(FormBuilder.class).to(FormBuilderImpl.class).in(Singleton.class);

        /* Utility for populating beans. */
        bind(HttpBeanUtil.class);

    }

}
