package com.ia.core;

import static com.google.common.base.Optional.fromNullable;
import static com.ia.log.LogUtil.getLogger;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import com.ia.beans.Error;
import com.ia.beans.Organisation;
import com.ia.log.Logger;
import com.ia.services.OrganisationService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class InjectableFilter implements Filter {

    protected final Logger logger = getLogger(getClass());

    protected OrganisationService organisationService;

    private final XStream xStream = new XStream(new DomDriver());

    @Override
    public void destroy() {
        /* Do nothing */
    }

    private Injector getInjector(final ServletContext context) {
        return (Injector) context.getAttribute(Injector.class.getName());
    }

    protected final Pair<String, Optional<Organisation>> getOrganisation(
            final HttpServletRequest request) {

        /* Most URLs have an organisation parameter */
        //if((request.getPathInfo())!="api/v1/dashboard2") {
            String organisationName = request.getParameter("organisation");

            /* API URLs use vendorId */
            if (isBlank(organisationName)) {
                organisationName = request.getParameter("vendorId");
            }

            return of(organisationName, getOrganisation(organisationName));

    }

    protected final Optional<Organisation> getOrganisation(final String organisationName) {
        return fromNullable(organisationService.getOrganisation(organisationName));
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {

        /* Inject all required instances. Subclasses must mark setters with @Inject */
        final Injector injector = getInjector(config.getServletContext());
        injector.injectMembers(this);

        /* Process annotations of commonly used XML beans */
        xStream.processAnnotations(Error.class);
    }

    protected final void sendError(final HttpServletResponse response, final Error error)
            throws IOException {

        xStream.processAnnotations(Error.class);

        response.setStatus(error.getCode().getStatusCode());
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(toXml(error));

        /* Done */
        return;
    }

    protected final void sendError(final HttpServletResponse response, final int errorCode)
            throws IOException {
        if (!response.isCommitted()) {
            response.sendError(errorCode);
            return;
        }
    }

    @Inject
    public void setOrganisationService(final OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    private final String toXml(final Object object) {
        return xStream.toXML(object);
    }
}
