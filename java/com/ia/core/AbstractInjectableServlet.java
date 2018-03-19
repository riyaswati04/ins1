package com.ia.core;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.google.inject.Injector;

public abstract class AbstractInjectableServlet extends HttpServlet {
    private static final long serialVersionUID = -8615348785342333400L;

    private Injector getInjector(final ServletContext context) {
        return (Injector) context.getAttribute(Injector.class.getName());
    }

    protected final <T> T getInstance(final ServletConfig config, final Class<T> klass) {
        final ServletContext context = config.getServletContext();
        return getInjector(context).getInstance(klass);
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        final ServletContext context = config.getServletContext();
        final Injector injector = getInjector(context);

        if (null == injector) {
            throw new ServletException("Guice Injector Not Found");
        }

        injector.injectMembers(this);

    }

}
