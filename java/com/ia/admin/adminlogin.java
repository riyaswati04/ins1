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

@Singleton
@ActionPath("/api/v1/alogin")


public class adminlogin extends AbstractAction
{

    public static String  sessionID=" ";
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

        String n=request.getParameter("username");
        String p=request.getParameter("userpass");
	    logger.info(n);
	    logger.info(p);

        HttpSession session = request.getSession();
        sessionID=session.getId();
        if(session!=null)
            session.setAttribute("name", n);

        if(validate(n, p)){
		    logger.info("in validate");
		     result.setSuccess(true);
                //result.setMessage(toJson(user));
                sendResponse(response, result);
               
	           // response.sendRedirect("http://www.google.com");
		    //return;
		    //response.setStatus(200,"success");
          // RequestDispatcher rd=request.getRequestDispatcher("http://localhost/ia/login");
                // response.getWriter().write("success");
                
        }
        else{
           // out.print("<p style=\"color:red\">Sorry username or password error</p>");
           // RequestDispatcher rd=request.getRequestDispatcher("alogin.html");
            //rd.include(request,response);
	logger.info("else");
	//response.sendRedirect("alogin.html");
	 result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        
	
        }

        out.close();
    }

    public static boolean validate(String name, String pass) {
        boolean status = false;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "plp";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "perfois@123";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + dbName, userName, password);

            pst = conn
                    .prepareStatement("select * from ia_admin_user where uname=? and upass=?");
            pst.setString(1, name);
            pst.setString(2, pass);

            rs = pst.executeQuery();
            status = rs.next();

        } catch (Exception e) {
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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return status;
    }




    @Override
    public boolean requiresLogin() {
        return false;
    }
}

