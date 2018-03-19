package com.ia.forms;

import static com.google.inject.assistedinject.FactoryProvider.newFactory;
import static com.google.inject.multibindings.MapBinder.newMapBinder;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.ia.core.forms.Form;
import com.ia.core.forms.FormFactory;
import com.ia.forms.impl.LoanFormImpl;
import com.ia.forms.impl.LoginFormImpl;
import com.ia.forms.impl.OrganisationFormImpl;
import com.ia.forms.impl.OrganisationIPRangeFormImpl;

public class FormsModule extends AbstractModule {

    @Override
    @SuppressWarnings("deprecation")
    protected void configure() {
        final MapBinder<Key<? extends Form>, FormFactory> mapBinder = newMapBinder(binder(),
                new TypeLiteral<Key<? extends Form>>() {}, new TypeLiteral<FormFactory>() {});

        /* Add binding */
        mapBinder.addBinding(Key.get(OrganisationForm.class))
                .toProvider(newFactory(FormFactory.class, OrganisationFormImpl.class));

        mapBinder.addBinding(Key.get(OrganisationIPRangeForm.class))
                .toProvider(newFactory(FormFactory.class, OrganisationIPRangeFormImpl.class));

        mapBinder.addBinding(Key.get(LoginForm.class))
                .toProvider(newFactory(FormFactory.class, LoginFormImpl.class));

        mapBinder.addBinding(Key.get(LoanForm.class))
                .toProvider(newFactory(FormFactory.class, LoanFormImpl.class));

    }
}
