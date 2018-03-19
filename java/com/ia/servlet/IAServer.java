package com.ia.servlet;

import static com.ia.log.LogUtil.getLogger;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.ia.actions.Action;
import com.ia.core.AbstractInjectableServlet;
import com.ia.log.Logger;
import com.ia.services.ActionService;

public class IAServer extends AbstractInjectableServlet {

    private static final String LOG_DISPATCHING_REQUEST =
            "Dispatching request to Action [path=%s,action=%s,ipaddress=%s]";

    private static final String LOG_ERROR_UNSAFE_HTTP_METHODS =
            "No support for HTTP [%s] method, path [%s]";

    private static final String LOG_EXCEPTION_IN_EXECUTION =
            "Exception executing action [PathInfo=%s,Action=%s,ipaddress=%s]";

    private static final String LOG_SERVLET_INITIALISED = "IA Servlet Initialised.";

    private static final String NO_ACTION_CLASS_FOR_REQUEST =
            "No Action class for request [PathInfo=%s].";

    private static final Pattern P_HTTP_SAFE_METHODS = compile("^(GET|POST)$", CASE_INSENSITIVE);

    private static final long serialVersionUID = -814886347325437568L;

    private ActionService actionService;

    private final Logger logger = getLogger(getClass());

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        processRequest(request, response);
    }

    private void executeAction(final HttpServletRequest request, final HttpServletResponse response,
            final Action action, final String path) throws IOException {

        final Class<? extends Action> actionClass = action.getClass();

        final String className = actionClass.getSimpleName();

        final String ipaddress = request.getRemoteAddr();

        /* Try to execute the action. */
        try {
            logger.info(LOG_DISPATCHING_REQUEST, path, className, ipaddress);
            action.execute(request, response);
        }
        catch (final Exception e) {
            logger.exception(LOG_EXCEPTION_IN_EXECUTION, e, path, className, ipaddress);
            response.sendError(SC_INTERNAL_SERVER_ERROR);
        }
    }

    public ActionService getActionService() {
        return actionService;
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        /* Super.init will inject ActionService instance. */
        super.init(config);
        logger.info(LOG_SERVLET_INITIALISED);
    }

    private boolean isSafeHTTPMethod(final String httpMethod) {
        return P_HTTP_SAFE_METHODS.matcher(httpMethod).matches();
    }

    private void processRequest(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        /* Locate an Action implementation to handle this request. */
        final Action action = actionService.getAction(request);

        final String path = request.getPathInfo();

        if (null == action) {
            logger.error(NO_ACTION_CLASS_FOR_REQUEST, path);
            response.sendError(SC_NOT_FOUND);
            return;
        }

        final String requestMethod = request.getMethod();

        /* Check if its a POST or GET, reject rest of them */
        if (!isSafeHTTPMethod(requestMethod)) {
            logger.error(LOG_ERROR_UNSAFE_HTTP_METHODS, requestMethod, path);
            response.sendError(SC_NOT_FOUND);
            return;
        }

        /* Execute action. */
        executeAction(request, response, action, path);
    }

    @Inject
    public void setActionService(final ActionService actionService) {
        this.actionService = actionService;
    }

}
