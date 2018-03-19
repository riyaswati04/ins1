package com.ia.servlet;

import static com.ia.beans.Error.create;
import static com.ia.enums.API_ERROR_CODE.AccessDenied;
import static com.ia.enums.API_ERROR_CODE.NotSignedUp;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.ia.admin.OrganisationFilter;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Optional;
import com.ia.actions.paths.Paths;
import com.ia.beans.Error;
import com.ia.beans.Organisation;
import com.ia.core.InjectableFilter;
import com.ia.enums.HTTP_METHOD;
import com.ia.enums.MODE;
import static com.ia.enums.MODE.NONE;

public final class VerifyPermissions extends InjectableFilter {

    private static final String LOG_METHOD_NOT_PERMITTED =
            "HTTP Method %s is not permitted for path %s";

    private static final String LOG_NO_SUCH_ORG = "No such organisation [organisationName=%s]";

    private static final String LOG_NOT_PERMITTED = "Organisation %s is not permitted to access %s";

    private Paths paths;
    private OrganisationFilter org_details=new OrganisationFilter();
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;



        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        /* Read pathInfo */
        final String pathInfo = request.getPathInfo();

        /* Get http method expected for this call */
        final HTTP_METHOD method = paths.getMethodExpected(pathInfo);

        /* Find the Organisation */
        final Pair<String, Optional<Organisation>> pair = getOrganisation(request);

        final String name = pair.getLeft();

        final Optional<Organisation> organisation = pair.getRight();

        final MODE permission = paths.getPermissionExpected(pathInfo);
        //int flag=0;



        /* No permission required? Continue. */

              if (NONE.equals(permission)) {
                                logger.info("New");
                               /* Call next filter in chain */
                                     filterChain.doFilter(servletRequest, servletResponse);

                             /* Done */
                                      return;
               }
        /* No such organisation? Log error and return HTTP 404. */
        if (!organisation.isPresent()) {

                /* Send error response */
                handleNotSignedUp(response, name);

                /* Done */
                return;



        }
            /* Organisation is not enabled */
            if (!organisation.get().isEnabled()) {

                handleNotPermitted(response, permission, name);

                /* Done */
                return;
            }

            /* Organisation has permission to access this path. Continue. */
            if (isPermitted(organisation.get(), permission)) {

                /* Call next filter in chain */
                filterChain.doFilter(servletRequest, servletResponse);

                /* Done */
                return;
            } else {

                /* Send error response */
                handleNotPermitted(response, permission, name);

                /* Done */
                return;
            }


    }

    private void handleNotPermitted(final HttpServletResponse response, final MODE permission,
            final String name) throws IOException {
        /* Log it */
        logger.error(LOG_NOT_PERMITTED, name, permission);

        /* Send Error */
        final Error error = create(AccessDenied);
        sendError(response, error);
    }

    private void handleNotSignedUp(final HttpServletResponse response, final String name)
            throws IOException {
        /* Log it */
        logger.error(LOG_NO_SUCH_ORG, name);

        /* Send Error */
        final Error error = create(NotSignedUp);
        sendError(response, error);
    }
    private void handlePermit(final HttpServletRequest request,final HttpServletResponse response)  throws IOException{
        try{
            org_details.execute(request,response);
        }
        catch(Exception e)
        {
            logger.info("exception caught");
        }

    }

    private boolean isPermitted(final Organisation organisation, final MODE permission) {
        // return organisation.isModeEnabled(permission);
        return true;
    }

    @Inject
    public void setPaths(final Paths paths) {
        this.paths = paths;
    }

}
