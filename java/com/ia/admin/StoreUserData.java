package com.ia.admin;

//import static com.ia.core.util.IAConstants.CONTENT_TYPE_APPLICATION_JSON;
//import static com.ia.log.LogUtil.getLogger;
//import static com.ia.util.JsonUtil.toJson;
//import static com.ia.util.http.HttpUtil.getSession;
//import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

//import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;




@Singleton
@ActionPath("/api/v1/alogin/dashboard")


public class StoreUserData extends AbstractAction
{


    private final Logger logger = getLogger(getClass());
    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
	final GenericResultBean result = new GenericResultBean(false);
        //logger.info(request);
        if(!((request.getMethod()).equals("POST"))){
            result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response,result);
            return;

        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session=request.getSession(false);


        if(! ((session.getId()).equals(adminlogin.sessionID)) )
        {
           logger.info("session invalidated");
           result.setMessage("error");
           response.setStatus(SC_BAD_REQUEST);
           sendResponse(response,result);
           return;

        }
                String org_id = request.getParameter("org");
                logger.info(org_id);
                int index = 0;
                index = org_id.indexOf("-");
                String organisation_id = org_id.substring(0, index);
                String email = request.getParameter("email");
                String pass = request.getParameter("password");
                pass = sha1(pass);
                logger.info(org_id);
                logger.info(email);
                logger.info(pass);


                if (udb_entry(organisation_id, email, pass)) {
                    logger.info("in udb_entry");
                    result.setSuccess(true);

                    sendResponse(response, result);


                } else {
                    logger.info("else");
                    result.setMessage("error");
                    response.setStatus(SC_BAD_REQUEST);
                    sendResponse(response, result);
                    return;


                }


        out.close();
    }

    public boolean udb_entry(String org_id,String email, String pass) {
       Connection conn = null;
       PreparedStatement pst = null;
       boolean status=false;
       logger.info("hello");

       String url = "jdbc:mysql://localhost:3306/";
       String dbName = "plp";
       String driver = "com.mysql.jdbc.Driver";
       String userName = "root";
       String password = "perfois@123";
       try {
           Class.forName(driver).newInstance();
           conn = DriverManager.getConnection(url + dbName, userName, password);
           pst = conn.prepareStatement("insert into ia_user(organisation_id,email_id,password) values(?,?,?)");
	   pst.setString(1, org_id);
           pst.setString(2, email);
	   pst.setString(3, pass);
           pst.executeUpdate();
	   logger.info("in try");
	   status=true;
           
           }
           

       catch (Exception e) {
           System.out.println(e);
       } finally {
           if (conn != null) {
               try {
                   conn.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
           if (pst != null) {
               try {
                   pst.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
           }  

        return status;
    }
 private String sha1(final String input) throws NoSuchAlgorithmException {
        final MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        final byte[] result = mDigest.digest(input.getBytes());
        final StringBuffer sb = new StringBuffer();

        for (final byte element : result) {
            sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }




   @Override
    public boolean requiresLogin() {
        return false;
    }
}

