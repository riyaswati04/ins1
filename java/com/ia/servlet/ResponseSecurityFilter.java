package com.ia.servlet;

import static com.ia.log.LogUtil.getLogger;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ia.log.Logger;

public final class ResponseSecurityFilter implements Filter {
    private static final String LOG_REJECTING_REQUEST =
            "Rejecting request as parameter %s contains restricted characters.";

    private final Logger logger = getLogger(getClass());

    private boolean containsIllicitCharacters(final String string) {
        return isNotEmpty(string) && (string.contains("%0d") || string.contains("%0a"));
    }

    @Override
    public void destroy() {
        /* Do nothing. */
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        /* Iterate through ALL parameters. */
        final Enumeration<?> allParameters = request.getParameterNames();

        while (allParameters.hasMoreElements()) {
            final String name = (String) allParameters.nextElement();
            final String[] values = request.getParameterValues(name);

            if (isEmpty(values)) {
                continue;
            }

            for (final String aValue : values) {
                if (containsIllicitCharacters(aValue)) {
                    logger.error(LOG_REJECTING_REQUEST, name);
                    response.sendError(SC_BAD_REQUEST);
                    return;
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        /* Do nothing. */
    }

}
