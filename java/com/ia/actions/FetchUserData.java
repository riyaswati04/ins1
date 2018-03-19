package com.ia.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.beans.GenericResultBean;
import com.ia.beans.User;
import com.ia.core.annotations.ActionPath;
import com.ia.services.SessionService;
import com.ia.util.DatabaseUtil;

@Singleton
@ActionPath("/api/v1/fetchData")
public class FetchUserData extends AbstractAction {

    private final SessionService service;

    private final DatabaseUtil dataBaseUtil;

    @Inject
    public FetchUserData(final DSLContext dslContext, final SessionService service,
            final DatabaseUtil dataBaseUtil) {
        super();
        this.service = service;
        this.dataBaseUtil = dataBaseUtil;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final User user = service.getUser(request.getSession().getId());

        final GenericResultBean result = new GenericResultBean(false);

        if (user == null) {
            result.setMessage("LOGIN_REQUIRED");
            sendResponse(response, result);
            return;
        }

        final String data = dataBaseUtil.getUserData(user.getUserId());

        result.setSuccess(true);
        result.setMessage(data);

        sendResponse(response, result);
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

}
