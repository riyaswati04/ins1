package com.ia.services;

import javax.servlet.http.HttpServletRequest;

import com.ia.beans.User;
import com.ia.core.annotations.Nullable;
import com.ia.core.forms.Form;
import com.ia.core.services.Service;

public interface SessionService extends Service {

    @Nullable
    User forceLogout(int userId);

    @Nullable
    User getCurrentlyLoggedInUser(HttpServletRequest request);

    @Nullable
    User getUser(String sessionId);

    void handleUserLogIn(String sessionId, User user);

    @Nullable
    User handleUserLogOut(String sessionId, Form form);

    @Nullable
    User handleUserSessionExpired(String sessionId);

    void invalidateUserCache(int userId);

    boolean isLoggedIn(int userId);

    boolean isLoggedIn(String sessionId);

}
