package com.ia.actions;

import static com.ia.log.LogUtil.getLogger;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.ia.beans.GenericResultBean;

public abstract class AbstractAction implements Action {

    private static final String LOG_DEBUG_WRITE_RESPONSE = "Write response response=%s";

    private static final String LOG_EXCEPTION_IN_WRITING_RESPONSE = "Could not write response [%s]";

    private static final String LOG_INVALID_REQUEST = "Invalid HTTP request, ipAddr=%s";

    private static final String LOG_UNHANDLED_HTTP_METHOD =
            "Unhandled HTTP Request Method [method=%s]";

    protected final com.ia.log.Logger logger = getLogger(getClass());

    protected final void logInvalidRequestAndSendError(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        logger.error(LOG_INVALID_REQUEST, request.getRemoteAddr());
        response.sendError(SC_BAD_REQUEST);
    }

    protected final void logUnsupportedHTTPMethodAndSendError(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        logger.error(LOG_UNHANDLED_HTTP_METHOD, request.getMethod());
        response.sendError(SC_BAD_REQUEST);
    }

    protected final void sendFailedResponse(final HttpServletResponse response) {
        try {
            response.sendError(SC_FORBIDDEN);
        }
        catch (final Exception ignore) {

        }
    }

    protected void sendResponse(final HttpServletResponse response,
            final GenericResultBean result) {
        final String message = new Gson().toJson(result);
        logger.debug(LOG_DEBUG_WRITE_RESPONSE, message);
        try {
            writeToResponse(response, message);
        }
        catch (final IOException e) {
            logger.error(e, LOG_EXCEPTION_IN_WRITING_RESPONSE, message);
            sendFailedResponse(response);
        }
    }

    protected void sendResponse(final HttpServletResponse response, final Object result) {
        final String message = new Gson().toJson(result);
        logger.debug(LOG_DEBUG_WRITE_RESPONSE, message);
        try {
            writeToResponse(response, message);
        }
        catch (final IOException e) {
            logger.error(e, LOG_EXCEPTION_IN_WRITING_RESPONSE, message);
            sendFailedResponse(response);
        }
    }

    protected final void writeToResponse(final HttpServletResponse response, String responseString)
            throws IOException {
        /* If null response put empty string */
        if (responseString == null) {
            responseString = "";
        }
        final PrintWriter writer = response.getWriter();
        writer.println(responseString);
        writer.flush();
    }

}
