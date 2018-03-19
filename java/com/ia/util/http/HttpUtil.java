package com.ia.util.http;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.ia.util.UrlEncodedQueryString.parse;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.tuple.Pair.of;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.tuple.Pair;

import com.ia.beans.Organisation;
import com.ia.util.UrlEncodedQueryString;

public final class HttpUtil {

    public static final String ATTR_CSRF_TOKEN = "csrfValidator.csrfToken";

    public static final String ATTR_CSRF_TOKEN_NAME = "csrfValidator.csrfToken.name";

    public static final String ATTR_ORIGINAL_QS = "urlrewrite.originalQueryString";

    public static final String ATTR_ORIGINAL_URI = "urlrewrite.originalURI";

    private static final String INVALID_REMOTE_ADDRESS = "Invalid remoteAddress %s";

    private static final String MSG_INVALID_ACTION_PATH =
            "Invalid actionPath %s (actionPath must start with /)";

    private static final String MSG_ORGANISATION_NON_NULL =
            "Organisation instance must not be null.";

    private static final String MSG_ORIGINAL_URI_MUST_BE_NON_EMPTY =
            "originalURI must be non empty.";

    private static final String MSG_REQUEST_NON_NULL = "HttpServletRequest must not be null.";

    private static final Pattern P_LEADING_UNDERSCORE = Pattern.compile("^_");

    private static final String TEMPLATE_URL = "/KuberaVault/enact%s";

    public static long convertRemoteAddressToLong(final String remoteAddress) {
        checkArgument(null != remoteAddress);

        final String[] parts = remoteAddress.split("\\.");
        checkState(null != parts && parts.length == 4, INVALID_REMOTE_ADDRESS, remoteAddress);

        return parseLong(parts[0]) * 16777216 + parseLong(parts[1]) * 65536
                + parseLong(parts[2]) * 256 + parseLong(parts[3]);
    }

    public static String deSensitise(final String paramName) {
        final Matcher matcher = P_LEADING_UNDERSCORE.matcher(paramName);
        return matcher.replaceFirst("");

    }

    // TODO: Fix this, organisation is needed only if it has to be a part of url
    public static String getActionUrl(final String organisation, final String actionPath) {
        checkArgument(isValid(actionPath), MSG_INVALID_ACTION_PATH, actionPath);
        return format(TEMPLATE_URL, actionPath);
    }

    private static String getAttribute(final HttpServletRequest request, final String name) {
        return (String) request.getAttribute(name);
    }

    public static Pair<String, String> getCSRFTokenNameAndValue(final HttpServletRequest request) {
        final String csrfTokenName = (String) request.getAttribute(ATTR_CSRF_TOKEN_NAME);
        final String csrfToken = (String) request.getAttribute(ATTR_CSRF_TOKEN);
        return of(csrfTokenName, csrfToken);
    }

    public static String getFormActionUrl(final HttpServletRequest request,
            final boolean formIsMultipart) throws URISyntaxException {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        final URI uri = new URI(getOriginalRequestUri(request));
        final UrlEncodedQueryString encodedQueryString = parse(uri);

        /* Is this form Multipart? */
        if (formIsMultipart) {
            /* Add/replace CSRF parameter to query string. */
            final String csrfTokenName = getAttribute(request, ATTR_CSRF_TOKEN_NAME);
            final String csrfToken = getAttribute(request, ATTR_CSRF_TOKEN);
            encodedQueryString.set(csrfTokenName, csrfToken);
        }

        /* Reconstruct the URI with new query string. */
        return encodedQueryString.apply(uri).toString();
    }

    public static String getOriginalQueryString(final HttpServletRequest request) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        return getAttribute(request, ATTR_ORIGINAL_QS);
    }

    public static String getOriginalRequestUri(final HttpServletRequest request) {
        final String originalURI = HttpUtil.getOriginalUri(request);
        checkState(isNotBlank(originalURI), MSG_ORIGINAL_URI_MUST_BE_NON_EMPTY);

        final String originalQueryString = getAttribute(request, ATTR_ORIGINAL_QS);
        final StringBuffer url = new StringBuffer(originalURI);

        if (isNotBlank(originalQueryString)) {
            url.append("?");
            url.append(originalQueryString);
        }

        return url.toString();
    }

    public static String getOriginalUri(final HttpServletRequest request) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        return getAttribute(request, ATTR_ORIGINAL_URI);
    }

    public static String getParameterValue(final HttpServletRequest request,
            final String parameterName) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        checkArgument(null != parameterName);
        return request.getParameter(parameterName);
    }

    public static HttpSession getSession(final HttpServletRequest request) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        return request.getSession();
    }

    public static String getSessionId(final HttpServletRequest request) {
        final HttpSession session = getSession(request);
        return session.getId();
    }

    public static HttpSession invalidateSessionAndCreateNew(final HttpServletRequest request,
            final int sessionTimeOut) {
        final HttpSession session = request.getSession();
        session.invalidate();
        request.getSession().setMaxInactiveInterval(sessionTimeOut);
        return request.getSession();
    }

    public static boolean isGETRequest(final HttpServletRequest request) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        final String method = request.getMethod();
        return isNotEmpty(method) && "GET".equals(method.toUpperCase());
    }

    public static boolean isPOSTRequest(final HttpServletRequest request) {
        checkArgument(null != request, MSG_REQUEST_NON_NULL);
        final String method = request.getMethod();
        return isNotEmpty(method) && "POST".equals(method.toUpperCase());
    }

    public static boolean isSensitiveParameter(final String paramName) {
        return isNotEmpty(paramName) && paramName.startsWith("_");

    }

    private static boolean isValid(final String actionPath) {
        return null != actionPath && actionPath.startsWith("/");
    }

    public static void redirect(final HttpServletResponse response, final Organisation organisation,
            final String actionPath) throws IOException {
        checkArgument(null != organisation, MSG_ORGANISATION_NON_NULL);
        response.sendRedirect(getActionUrl(organisation.getOrganisationName(), actionPath));
    }

    public static void redirect(final HttpServletResponse response, final String organisationName,
            final String actionPath) throws IOException {
        response.sendRedirect(getActionUrl(organisationName, actionPath));
    }
}
