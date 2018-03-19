package com.ia.admin;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;

import com.google.gson.Gson;
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


import static java.sql.DriverManager.getConnection;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static com.ia.log.LogUtil.getLogger;

//import java.sql.DriverManager;
import java.sql.SQLException;
import com.ia.beans.GenericResultBean;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.Object;
import java.util.ArrayList;
//import java.time.localDate;

@Singleton
@ActionPath("/api/v1/alogin/dashboard_dropdown")

public class OrganisationFilter extends AbstractAction {
    public ArrayList<OrganisationBean> conv = new ArrayList<OrganisationBean>();

    private final Logger logger = getLogger(getClass());

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);
        if(!((request.getMethod()).equals("POST"))){
            result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response,result);
            return;

        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        con();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(new Gson().toJson(conv));
        out.close();

    }

    public void con() {
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
            conn = getConnection(url + dbName, userName, password);
            String query = "Select * from ia_organisations;";


            pst = conn.prepareStatement(query);
            rs = pst.executeQuery();
            conv.clear();
            while (rs.next()) {
                OrganisationBean c = new OrganisationBean(rs.getString("organisation_id"), rs.getString("organisation_name"));
                String org_id = rs.getString("organisation_id");
                String org_name = rs.getString("organisation_name");
                conv.add(c);
            }

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


    }


    @Override
    public boolean requiresLogin() {
        return false;
    }
}
