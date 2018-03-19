package com.ia.core;

import static com.google.inject.Guice.createInjector;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.ia.core.modules.CoreModule;

public final class BootstrapContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return createInjector(
                /* Core */
                new CoreModule());
    }
}
