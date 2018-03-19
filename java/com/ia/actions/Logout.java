package com.ia.actions;

import static com.ia.log.LogUtil.getLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.beans.GenericResultBean;
import com.ia.core.annotations.ActionPath;
import com.ia.log.Logger;
import com.ia.services.SessionService;

@Singleton
@ActionPath("/api/v1/logout")
public class Logout extends AbstractAction {

    private final SessionService service;

    private final Logger logger = getLogger(getClass());

    @Inject
    public Logout(final SessionService service) {
        super();
        this.service = service;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        logger.debug("ID" + request.getSession().getId());

        GenericResultBean result = new GenericResultBean(false);

        service.handleUserLogOut(request.getSession().getId(), null);

        result.setSuccess(true);
        result.setMessage("User Logged Out");

        sendResponse(response, result);
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }
}
