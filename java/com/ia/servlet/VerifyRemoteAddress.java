package com.ia.servlet;

import static com.ia.enums.MODE.NONE;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.ia.actions.paths.Paths;
import com.ia.beans.Organisation;
import com.ia.enums.MODE;

public final class VerifyRemoteAddress extends com.ia.core.InjectableFilter {

    private static final String LOG_IP_NOT_PERMITTED =
            "This IP address is not permitted to make a request [organisation=%s,remoteAddress=%s,mode=%s]";

    private static final String LOG_NO_SUCH_ORG = "No such organisation [organisationName=%s]";

    private Paths paths;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        /* Read pathInfo */
        final String pathInfo = request.getPathInfo();

        /* Find the Organisation */
        final Pair<String, Optional<Organisation>> pair = getOrganisation(request);

        final String name = pair.getLeft();

        final Optional<Organisation> organisation = pair.getRight();
        final MODE permission = null;
        int flag=0;

        if(organisation.isPresent()){
            flag=1;
            logger.info("Flag set to 1");
        }


        if(flag==0&&(null == permission || NONE.equals(permission))){
            logger.info("New");
            /* Call next filter in chain */
            filterChain.doFilter(servletRequest, servletResponse);

            /* Done */
            return;
        }




            /* No such organisation? Log error and return HTTP 404. */
            if (!organisation.isPresent()) {

                /* Log it */
                logger.error(LOG_NO_SUCH_ORG, name);

                /* Send HTTP 404 */
                sendError(response, SC_NOT_FOUND);

                /* Done */
                return;

            }

        /* Did this request originate from a permitted remote address? */
        final String remoteAddress = request.getRemoteAddr();

        if (!isExpectedRemoteAddress(organisation.get(), null, remoteAddress)) {

            /* Log it. */
            logger.error(LOG_IP_NOT_PERMITTED, name, remoteAddress, null);

            /* Send HTTP 403 Forbidden */
            sendError(response, SC_FORBIDDEN);

            /* Done */
            return;
        }

        /* All OK. */
        filterChain.doFilter(request, response);
        return;
    }

    private boolean isExpectedRemoteAddress(final Organisation organisation, final MODE permission,
            final String remoteAddress) {
        if (permission == null) {
            return true;
        }

        return organisation.isExpectedRemoteAddress(permission, remoteAddress);
    }

    @Inject
    public void setPaths(final Paths paths) {
        this.paths = paths;
    }

}
