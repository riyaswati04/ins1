package com.ia.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ia.log.Logger;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

//import com.google.inject.Inject;
import com.google.inject.Singleton;
//import com.ia.beans.GenericResultBean;
//import com.ia.beans.User;
import com.ia.core.annotations.ActionPath;
//import com.ia.core.forms.FormBuilder;
//import com.ia.fetchdata.UserCredentialAuthenticate;
//import com.ia.forms.LoginForm;
//import com.ia.log.Logger;
//import com.ia.services.SessionService;
import com.ia.actions.AbstractAction;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static com.ia.log.LogUtil.getLogger;

//import java.sql.DriverManager;
//import java.sql.SQLException;
import com.ia.beans.GenericResultBean;

import java.io.PrintWriter;

@Singleton
@ActionPath("/api/v1/adminLogout")


public class LogOut extends AbstractAction {


    private final Logger logger = getLogger(getClass());

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final GenericResultBean result = new GenericResultBean(false);
        response.setContentType("application/json");
        PrintWriter out=response.getWriter();


        HttpSession session=request.getSession();

        session.invalidate();

        result.setSuccess(true);

        sendResponse(response, result);
        //out.print("You are successfully logged out!");

        out.close();

    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

}
