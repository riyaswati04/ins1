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

import java.sql.*;

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
import java.lang.Object;
//import java.time.localDate;



@Singleton
@ActionPath("/api/v1/alogin/dashboard_org")


public class StoreOrganisation extends AbstractAction
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


        String org_name=request.getParameter("org");
        logger.info(org_name);
	

        
        if(odb_entry(org_name)){
		    logger.info("in odb_entry");
		     result.setSuccess(true);

                sendResponse(response, result);
               
	                          
        }
        else{
           	logger.info("else");
		result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        
	
        }

        out.close();
    }

    public boolean odb_entry(String org_name) {
       Connection conn = null;
       PreparedStatement pst = null;
       Statement st=null;
       boolean status=false;
       logger.info("hello");
       

       String url = "jdbc:mysql://localhost:3306/";
       String dbName = "plp";
       String driver = "com.mysql.jdbc.Driver";
       String userName = "root";
       String password = "perfois@123";
       logger.info("abc");
       try {
           Class.forName(driver).newInstance();
           conn = DriverManager.getConnection(url + dbName, userName, password);
	   logger.info("after conn");
           pst = conn.prepareStatement("insert into ia_organisations(organisation_name,license_end_date) values(?,now())");
	   pst.setString(1, org_name);
	  // pst.setString(2,"-----BEGIN RSA PRIVATE KEY-----\\MIIBPQIBAAJBAOA7ZMwwq9OY8todlYBwsziy1v7MLUmVza7Qdp+44G7eI0huBTLR\\do76VLoiY+zLg0jImwUsFVq474bK7HvDoOUCAwEAAQJBAJTsHWfXs2bXIANovpAN\\SZqQfGXBKRrEGVTPMtmlqbk3JJcpNMK9gxqWbeo+ZmPBgHS/CmSJxvOTnNOiLsBe\\lwECIQD+g2Mum+j77ejJZHZTAzTXVeq7LT8NYQBJbBlW3cptoQIhAOGKuN+ftI7E\\WUjMdzX3GuAv5YOBd1sz/gZvxSfsTsTFAiEAg0Yrgx/htQfKOQ47RafyulrTXsYA\\rprotfYuv7JYNeECIQCp6TP1Y/9GPq10pnR4dzwMAIlLVNFyJ+0LNFC3DtMYcQIh\\AIeLvybuqaaaNBzrXQrjgHGANrn2oHyhdM5FUK5nq3n5\\-----END RSA PRIVATE KEY-----");
	  // pst.setString(3,"-----BEGIN RSA PRIVATE KEY-----MIIBOQIBAAJBAMHaKzJ7p8abF02GQG8beU4WKg6L3RBAgTQjetNDawqyk4lkU3kIySkJWWFR7pJsf3X2Mr8ZMU/u6KqHgDw6E/cCAwEAAQJAI1ICDuey8R/vBgQRF211E8I8FXxsYfquz/Yq+fVNupvvylaNjqKADUz5N3OdQw+fkNugyXSsbQa2U1bIBLvsmQIhAOTcgqmvME7NHPsTQEYlk5x2Pjo9QdCVNP9RwS3twwqjAiEA2Nbk4Zxk7gv6St9R7/klEQn7z3460DIjFRomXAHYGp0CIAkqr+0eAEBXxN5logBtRuFQdyOcCoPRRiMn4iX1zQcjAiBP8nMy53mi9tGSd2H7a02KSbcI2o6Ool2i8yA/fgKSVQIgbs/uFB9DwBaseokvC3lma4Ylm7dF0zzYR66lY2RC0wU=-----END RSA PRIVATE KEY-----");
	  // pst.setDate(4,Date.valueOf(java.time.localDate.now()));

	   pst.executeUpdate();
           	   logger.info("xyz");
          pst=conn.prepareStatement("UPDATE ia_organisations SET insights_key = (SELECT insights_key FROM(select * from ia_organisations) as org WHERE organisation_name=?);");
	  logger.info("after prepare");
           pst.setString(1,"acme");
	   logger.info("after setString");
           logger.info("before");
           pst.executeUpdate();
	  // st=conn.createStatement();
	   //st.executeQuery(query1);
           logger.info("after");
          // String query="UPDATE ia_organisations SET ia_organisations.key = (SELECT ia_organisations.key FROM(select * from ia_organisations) as org2 WHERE organisation_name='acme')";
           
	  // st=conn.createStatement();
	   //st.executeQuery(query); 
	    pst=conn.prepareStatement("UPDATE ia_organisations SET `key` = (SELECT `key` FROM(select * from ia_organisations) as org4 WHERE organisation_name=?);");
           pst.setString(1,"acme");
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




   @Override
    public boolean requiresLogin() {
        return false;
    }
}

