package com.ia.services.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.http.HttpUtil.getSessionId;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.beans.User;
import com.ia.core.forms.Form;
import com.ia.log.Logger;
import com.ia.services.SessionService;

@Singleton
public final class SessionServiceImpl implements SessionService {
    private static final Object LOCK = new Object();

    private static final String LOG_FORCE_LOGGED_USER_OUT =
            "Logged user out by force. [userId=%s,userName=%s]";

    private static final String LOG_LOGGED_USER_OUT =
            "Logged user out. [sessionId=%s,userId=%s,userName=%s]";

    private static final String LOG_STARTED = "Started.";

    private static final String LOG_STOPPING = "Stopping. Cleared user map.";

    private static final String LOG_USER_ADDED_TO_MAP =
            "User added to userMap [userId=%s,sessionId=%s]";

    private static final String LOG_USER_ALREADY_LOGGED_IN =
            "User is already logged in. Replacing. [userId=%s,sessionId=%s]";

    // private static final String MSG_LOGGED_OUT_FORCE = "Logged out by force";

    // private static final String MSG_LOGGED_OUT_ON_SESSION_EXPIRY = "Logged out on session
    // expiry";

    private static final String MSG_SESSION_ID_NOT_NULL = "Session id must not be blank";

    private static final String MSG_USER_ID_GT_0 = "UserId must be greater than 0";

    private static final String MSG_USER_NOT_NULL = "User must not be null";

    private final Logger logger = getLogger(getClass());

    private final BiMap<String, Integer> sessionIdToUserIdMap;

    private final BiMap<Integer, User> userCache;

    @Inject
    public SessionServiceImpl() {
        super();
        sessionIdToUserIdMap = HashBiMap.create();
        userCache = HashBiMap.create();
    }

    private void addUserToUserMaps(final String sessionId, final int userId, final User user) {
        synchronized (LOCK) {
            sessionIdToUserIdMap.put(sessionId, userId);
            userCache.put(userId, user);
        }
    }

    private void clearSessionStorage() {
        synchronized (LOCK) {
            sessionIdToUserIdMap.clear();
        }
    }

    private void clearUserCacheStorage() {
        synchronized (LOCK) {
            userCache.clear();
        }
    }

    @Override
    public User forceLogout(final int userId) {
        synchronized (LOCK) {

            /* Not logged in? Return. */
            if (!isLoggedIn(userId)) {
                return null;
            }

            /* Log user out. */
            final User user = retrieveUserFromCache(userId);
            removeUserFromMaps(userId);

            /* Log it */
            logger.info(LOG_FORCE_LOGGED_USER_OUT, userId, user.getEmailId());

            return user;
        }
    }

    @Override
    public User getCurrentlyLoggedInUser(final HttpServletRequest request) {
        final String sessionId = getSessionId(request);
        return getUser(sessionId);
    }

    @Override
    public User getUser(final String sessionId) {
        synchronized (LOCK) {
            final Integer userId = sessionIdToUserIdMap.get(sessionId);
            return null != userId ? retrieveUserFromCache(userId) : null;
        }
    }

    private void handleUserLogIn(final String sessionId, final int userId, final User user) {
        synchronized (LOCK) {
            if (isLoggedIn(sessionId, userId)) {
                logger.warn(LOG_USER_ALREADY_LOGGED_IN, userId, sessionId);
                removeUserFromMaps(userId);
            }

            addUserToUserMaps(sessionId, userId, user);
            logger.info(LOG_USER_ADDED_TO_MAP, userId, sessionId);
        }
    }

    @Override
    public synchronized void handleUserLogIn(final String sessionId, final User user) {
        checkArgument(isNotBlank(sessionId), MSG_SESSION_ID_NOT_NULL);
        checkArgument(null != user, MSG_USER_NOT_NULL);

        final int userId = user.getUserId();
        checkState(userId > 0, MSG_USER_ID_GT_0);

        handleUserLogIn(sessionId, userId, user);
    }

    @Override
    public synchronized User handleUserLogOut(final String sessionId, final Form form) {

        /* SessionId is mandatory. */
        checkArgument(isNotBlank(sessionId), MSG_SESSION_ID_NOT_NULL);

        synchronized (LOCK) {

            /* Not logged in? Return. */
            if (!isLoggedIn(sessionId)) {
                return null;
            }

            /* Log user out. */
            final User user = getUser(sessionId);
            final int userId = user.getUserId();
            removeUserFromMaps(userId);

            /* Log it */
            logger.info(LOG_LOGGED_USER_OUT, sessionId, userId, user.getEmailId());

            return user;

        }

    }

    @Override
    public synchronized User handleUserSessionExpired(final String sessionId) {
        /* SessionId is mandatory. */
        checkArgument(isNotBlank(sessionId), MSG_SESSION_ID_NOT_NULL);

        synchronized (LOCK) {

            /* Not logged in? Return. */
            if (!isLoggedIn(sessionId)) {
                return null;
            }

            /* Log user out. */
            final User user = getUser(sessionId);
            final int userId = user.getUserId();
            removeUserFromMaps(userId);

            /* Log it */
            logger.info(LOG_LOGGED_USER_OUT, sessionId, userId, user.getEmailId());

            return user;

        }

    }

    @Override
    public void invalidateUserCache(final int userId) {
        synchronized (LOCK) {
            userCache.remove(userId);
        }
    }

    @Override
    public boolean isLoggedIn(final int userId) {
        return isLoggedIn(null, userId);
    }

    @Override
    public boolean isLoggedIn(final String sessionId) {
        return isLoggedIn(sessionId, null);
    }

    private boolean isLoggedIn(final String sessionId, final Integer userId) {
        synchronized (LOCK) {
            return sessionIdToUserIdMap.containsKey(sessionId)
                    || sessionIdToUserIdMap.containsValue(userId);
        }
    }

    private void removeUserFromMaps(final int userId) {
        synchronized (LOCK) {
            final String sessionId = sessionIdToUserIdMap.inverse().remove(userId);
            sessionIdToUserIdMap.remove(sessionId);
            userCache.remove(userId);
        }
    }

    @Override
    public void restart() throws Exception {
        clearSessionStorage();
        clearUserCacheStorage();
    }

    private User retrieveUserFromCache(final Integer userId) {
        return userCache.get(userId);
    }

    @Override
    public void start() throws Exception {
        logger.info(LOG_STARTED);
    }

    @Override
    public void stop() throws Exception {
        clearSessionStorage();
        logger.info(LOG_STOPPING);
    }

}
