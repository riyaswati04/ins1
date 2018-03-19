package com.ia.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Ensures that any request parameters whose names start with 'flash.' are available for the next
 * request too.
 */
public class FlashScopeFilter implements Filter {
    public static final String ATTR_FLASH_MESSAGE = "flash.message";

    public static final String SESSION_KEY_FLASH = "_flashMessage";

    @Override
    public void destroy() {
        /* Do nothing. */
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        /* Move any flash messages from Session to request (set attribute). */
        moveFlashMessageIfAnyFromSessionToRequest(request);

        /* Call filterChain. */
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private HttpSession getSession(final HttpServletRequest request) {
        /* Use `false` to get existing session. */
        return request.getSession(false);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        /* Do nothing. */
    }

    private void moveFlashMessageIfAnyFromSessionToRequest(final HttpServletRequest request) {
        final HttpSession session = getSession(request);

        /* No session; nothing to do here. */
        if (null == session) {
            return;
        }

        final Object flashMessage = session.getAttribute(SESSION_KEY_FLASH);

        /* No flash message. Return. */
        if (null == flashMessage) {
            return;
        }

        /* Set flashMessage as request's attribute. */
        request.setAttribute(ATTR_FLASH_MESSAGE, flashMessage);

        /* Remove flashMessage from session. */
        session.removeAttribute(SESSION_KEY_FLASH);
    }
}
