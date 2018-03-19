package com.ia.services.impl;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.inject.name.Names.named;
import static com.ia.beans.FlashMessage.warn;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.servlet.FlashScopeFilter.SESSION_KEY_FLASH;
import static com.ia.util.http.HttpUtil.getSession;
import static com.ia.util.http.HttpUtil.getSessionId;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.ia.actions.Action;
import com.ia.beans.FlashMessage;
import com.ia.log.Logger;
import com.ia.services.ActionService;
import com.ia.services.SessionService;

public abstract class AbstractActionService implements ActionService {

    private static final String LOG_DEREGISTERED_ALL_ACTIONS =
            "Stopping. Deregistered all actions.";

    private static final String LOG_DERIVED_ACTION_PATH = "Derived action path [path=%s]";

    private static final String LOG_STARTED_REGISTERED_ACTION_PATHS =
            "Started. Registered %s action paths.";

    private static final String MSG_LOGIN_FIRST = "Please login to the application first";

    private static final String MSG_PATH_INFO_MIN_ELEMENTS =
            "PathInfo must contain at least one path element";

    private static final String MSG_PATH_INFO_MUST_BE_NON_BLANK = "PathInfo must be non-blank.";

    private static final String MSG_PATH_INFO_STARTS_WITH = "PathInfo must start with '/'";

    private final Set<String> allActionPaths;

    private final Injector injector;

    private final Logger logger = getLogger(getClass());

    private final SessionService sessionService;

    public AbstractActionService(final Injector injector, final SessionService sessionService) {
        super();
        allActionPaths = newHashSet();
        this.injector = injector;
        this.sessionService = sessionService;
    }

    private boolean actionDoesNotRequireLogin(final Action action) {
        return !action.requiresLogin();
    }

    private boolean actionRequiresLoginAndUserIsNotLoggedIn(final Action action,
            final HttpServletRequest request) {
        /* Action requires login; user is not logged in. */
        return action.requiresLogin() && !isLoggedIn(request);
    }

    private void clearActionMap() {
        allActionPaths.clear();
    }

    private void createAndSetFlashMessageAskingUserToLogin(final HttpServletRequest request) {

        /* Create flash message */
        final FlashMessage message = warn(MSG_LOGIN_FIRST);

        /* Set it. */
        final HttpSession session = getSession(request);
        session.setAttribute(SESSION_KEY_FLASH, message);

    }

    private String deriveActionPath(final String pathInfo) {
        checkArgument(isNotBlank(pathInfo), MSG_PATH_INFO_MUST_BE_NON_BLANK);
        checkArgument(pathInfo.startsWith("/"), MSG_PATH_INFO_STARTS_WITH);

        final String[] parts = pathInfo.split("/");
        checkPositionIndex(1, parts.length, MSG_PATH_INFO_MIN_ELEMENTS);
        return on('/').join(parts);
    }

    @Override
    public final Action getAction(final HttpServletRequest request) {
        final String path = deriveActionPath(request.getPathInfo());
        logger.debug(LOG_DERIVED_ACTION_PATH, path);

        /* No such action? Return. */
        if (!allActionPaths.contains(path)) {
            return null;
        }

        /* Lookup the action corresponding to the path. */
        final Action action = getAction(path);

        /* Action doesn't require login */
        if (actionDoesNotRequireLogin(action)) {
            return action;

        }
        /* Action requires login and user is not logged in. */
        else if (actionRequiresLoginAndUserIsNotLoggedIn(action, request)) {

            /* Show a warning message. */
            createAndSetFlashMessageAskingUserToLogin(request);

            // Redirect to login page
            return null;

        }
        return action;
    }

    private Action getAction(final String actionPath) {
        final Key<Action> key = Key.get(Action.class, named(actionPath));
        return injector.getInstance(key);
    }

    private boolean isLoggedIn(final HttpServletRequest request) {
        final String sessionId = getSessionId(request);
        return sessionService.isLoggedIn(sessionId);
    }

    protected abstract void registerAllPaths() throws Exception;

    protected final void registerPath(final String actionPath) {
        allActionPaths.add(actionPath);
    }

    @Override
    public final void restart() throws Exception {}

    @Override
    public final void start() throws Exception {
        registerAllPaths();
        logger.info(LOG_STARTED_REGISTERED_ACTION_PATHS, allActionPaths.size());
    }

    @Override
    public final void stop() throws Exception {
        clearActionMap();
        logger.info(LOG_DEREGISTERED_ALL_ACTIONS);
    }
}
