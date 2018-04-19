package com.ia.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.ia.beans.User;
import com.ia.core.forms.FormBuilder;
import com.ia.log.Logger;



import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;

import com.ia.actions.AbstractAction;

import static com.ia.util.JsonUtil.toJson;
import static com.ia.util.http.HttpUtil.getSession;
import static com.ia.log.LogUtil.getLogger;
import static org.jooq.tools.StringUtils.isEmpty;


import com.ia.beans.GenericResultBean;
import com.ia.services.SessionService;
import com.ia.util.AdminDatabaseUtil;

@Singleton
@ActionPath("/api/v1/adminLogin")


public class AdminLogin extends AbstractAction
{

    private final Logger logger = getLogger(getClass());
    public ResultSet rs = null;
    public ResultSet us = null;


    public User user=null;

    private final SessionService service;
    private final AdminDatabaseUtil dbConnection;
    Pattern pattern;
    Matcher matcher;


    @Inject
    public AdminLogin(final SessionService service,final AdminDatabaseUtil dbConnection){

        super();
        this.service = service;
        this.dbConnection=dbConnection;

    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);

        String emailPattern ="^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";


        response.setContentType("application/json");


        String username = request.getParameter("username");
        pattern = Pattern.compile(emailPattern);

        String password = request.getParameter("password");


        if(isEmpty(username)||isEmpty(password)||isEmpty(password)){
            result.setSuccess(false);
            result.setMessage("Either email or password or organisation  is null");
            sendResponse(response,result);
            return;
        }


        final HttpSession session = getSession(request);
        user=new User();
        if(validateEmail(username)) {
            user = (validate(username, password));
        }
        if (user != null) {
            logger.info("User not null");
            service.handleUserLogIn(session.getId(), user);

            if (service.isLoggedIn(request.getSession().getId())) {
                result.setSuccess(true);
                result.setMessage(toJson(user));
                sendResponse(response, result);
            }
        } else {
            result.setSuccess(false);
            result.setMessage("No User Available");
            sendResponse(response, result);
        }



    }

    public  User validate(String name, String pass) {
        PreparedStatement pst = null;

        try {
            dbConnection.connectDatabase();
            pst = dbConnection.conn.prepareStatement("select * from ia_admin_user where uname=? and upass=?");
            pst.setString(1, name);
            pst.setString(2, pass);
            rs = pst.executeQuery();
            String email;
            int userid;
            while (rs.next()) {
                email = rs.getString(2);
                user.setEmailId(email);
                userid = rs.getInt(1);
                user.setUserId(userid);
            }
        }

        catch (Exception e) {
            logger.error(e,"Exception caught");
        } finally {
            if (dbConnection.conn != null) {
                try {
                    dbConnection.conn.close();
                } catch (SQLException e) {
                    logger.error(e,"SQL Exception caught");

                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    logger.error(e,"Exception caught");

                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error(e,"Exception caught SQL");

                }
            }
        }

        return user;
    }
    public boolean validateEmail( String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }


    @Override
    public boolean requiresLogin() {
        return false;
    }
}

