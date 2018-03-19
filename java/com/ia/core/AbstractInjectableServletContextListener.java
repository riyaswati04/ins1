package com.ia.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Injector;

public abstract class AbstractInjectableServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final Injector injector = getInjector(sce);
        injector.injectMembers(this);
    }

    private Injector getInjector(final ServletContext context) {
        return (Injector) context.getAttribute(Injector.class.getName());
    }

    protected final Injector getInjector(final ServletContextEvent servletContextEvent) {
        final ServletContext context = servletContextEvent.getServletContext();
        return getInjector(context);
    }

    protected final <T> T getInstance(final ServletContextEvent servletContextEvent,
            final Class<T> klass) {
        return getInjector(servletContextEvent).getInstance(klass);
    }

}
