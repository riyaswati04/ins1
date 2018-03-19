package com.ia.servlet;

import static com.google.common.collect.Lists.newArrayList;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.http.HttpUtil.getSessionId;
import static com.ia.util.http.HttpUtil.isSensitiveParameter;
import static java.lang.String.format;
import static java.util.Collections.list;
import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;
import com.ia.log.Logger;

public final class RequestLogger implements Filter {
    private static final Pattern P_PERCENTAGE = compile("%");

    private static final String TEMPLATE_LOG_STRING = "RemoteAddress=%s; SessionId=%s; %s %s";

    private static final String TEMPLATE_PARAM_VALUE = "%s=%s";

    private final Logger logger = getLogger(getClass());

    @Override
    public void destroy() {}

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain filterChain) throws IOException, ServletException {
        logger.info(generateLogString((HttpServletRequest) request));
        filterChain.doFilter(request, response);
    }

    private String escapePercentageSign(final String input) {
        /* Replace all % with %% so that they work with String.format() method. */
        return P_PERCENTAGE.matcher(input).replaceAll("%%");
    }

    protected String generateLogString(final HttpServletRequest request) {
        /* Remote address, session ID, HTTP method, request details */
        final String method = request.getMethod();
        return format(TEMPLATE_LOG_STRING, request.getRemoteAddr(), getSessionId(request), method,
                getRequestDetails(request));
    }

    private String getParameterDetails(final HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        final Enumeration<String> parameterNames = request.getParameterNames();
        if (!parameterNames.hasMoreElements()) {
            return "";
        }

        final List<String> listOfParamaterNames = list(parameterNames);
        sort(listOfParamaterNames);

        final List<String> namesAndValues = newArrayList();
        for (final String name : listOfParamaterNames) {
            /* Don't log sensitive parameters (identified by an _ prefix). */
            if (isSensitiveParameter(name)) {
                continue;
            }

            final String[] values = request.getParameterValues(name);
            namesAndValues.add(format(TEMPLATE_PARAM_VALUE, escapePercentageSign(name),
                    valuesToString(values)));
        }

        return format("?%s", Joiner.on("&").join(namesAndValues));
    }

    private String getRequestDetails(final HttpServletRequest request) {
        final String template = "%s%s";
        final String servletPath = request.getServletPath();
        final String pathInfo = request.getPathInfo();
        return format(template, servletPath, pathInfo);
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {}

    private String valuesToString(final String[] values) {
        String result = "";

        switch (values.length) {
            case 0:
                result = "";
                break;

            case 1:
                result = values[0];
                break;

            default:
                result = Arrays.toString(values);
                break;
        }

        return escapePercentageSign(result);
    }
}
