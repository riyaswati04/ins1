package com.ia.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.AbstractAction;
import com.ia.beans.GenericResultBean;
import com.ia.core.annotations.ActionPath;
import com.ia.generated.tables.IaOrganisations;
import com.ia.generated.tables.IaUser;
import com.ia.log.Logger;
import com.ia.util.AdminDatabaseUtil;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ia.log.LogUtil.getLogger;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@Singleton

@ActionPath("/api/v1/toggleUser")

public class UserStatus extends AbstractAction {


    private final Logger logger = getLogger(getClass());
    private final DSLContext dslContext;



    @Inject
    public UserStatus(final DSLContext dslContext){
        super();
        this.dslContext=dslContext;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);

        response.setContentType("application/json");
        String email =request.getParameter("user");

        email=email.trim();
        String status=request.getParameter("status");
        if(!(status.equalsIgnoreCase("NORMAL") || status.equalsIgnoreCase("LOCKED") ) )
        {
            result.setSuccess(false);
            result.setMessage("Invalid Input");
            sendResponse(response,result);
        }

        if(email.isEmpty() || email.equals(" "))
        {
            result.setSuccess(false);
            result.setMessage("Invalid Input");
            sendResponse(response,result);

        }

        if (updateStatus(email,status))
        {
            result.setSuccess(true);
            sendResponse(response, result);
        } else {
            result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);

        }


    }

    public boolean updateStatus(String email,String status) {
        boolean stat = false;
        try {
            dslContext.update(

                    IaUser.IA_USER)

                    .set(IaUser.IA_USER.STATUS, status)

                    .where(IaUser.IA_USER.EMAIL_ID.equal(email))

                    .execute();

            stat=true;


        } catch (Exception e) {
            logger.error(e, "Exception caught");
        }
        return stat;



    }

    @Override
    public boolean requiresLogin() {
        return true;
    }
}