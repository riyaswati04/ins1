package com.ia.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

import com.google.inject.Inject;
import com.ia.log.Logger;

import com.google.inject.Singleton;

import com.ia.core.annotations.ActionPath;

import com.ia.actions.AbstractAction;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static com.ia.log.LogUtil.getLogger;


import com.ia.beans.GenericResultBean;
import com.ia.services.impl.OrganisationServiceImpl;
import com.ia.util.AdminDatabaseUtil;
import org.jooq.tools.StringUtils;


@Singleton
@ActionPath("/api/v1/addOrganisation")


public class StoreOrganisation extends AbstractAction
{

    private final AdminDatabaseUtil dbConnection;
    private final OrganisationServiceImpl organisationCache;
    private final Logger logger = getLogger(getClass());

    @Inject
    public StoreOrganisation(final AdminDatabaseUtil dbConnection, final OrganisationServiceImpl organisationCache){
        super();
        this.dbConnection=dbConnection;
        this.organisationCache=organisationCache;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
	final GenericResultBean result = new GenericResultBean(false);


        response.setContentType("application/json");
        String organisationName=request.getParameter("organisation");

        if(StringUtils.isEmpty(organisationName)){
            result.setSuccess(false);
            result.setMessage("Organisation  is null");
            sendResponse(response,result);
            return;
        }


        if(addOrganisation(organisationName)){
		     result.setSuccess(true);
             organisationCache.restart();
		     sendResponse(response, result);
        }
        else{
		    result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        
	
        }


    }

    public boolean addOrganisation(String organisationName) {
       PreparedStatement pst = null;
       boolean status=false;

       try {
           dbConnection.connectDatabase();
           pst = dbConnection.conn.prepareStatement("insert into ia_organisations(organisation_name,license_end_date) values(?,now())");
	       pst.setString(1, organisationName);
	       pst.executeUpdate();
           pst=dbConnection.conn.prepareStatement("UPDATE ia_organisations SET insights_key = (SELECT insights_key FROM(select * from ia_organisations) as org WHERE organisation_name=?);");
           pst.setString(1,"acme");
           pst.executeUpdate();
	       pst=dbConnection.conn.prepareStatement("UPDATE ia_organisations SET `key` = (SELECT `key` FROM(select * from ia_organisations) as org4 WHERE organisation_name=?);");
           pst.setString(1,"acme");
	       pst.executeUpdate();
	       status=true;
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
                   logger.error(e," Exception caught");

               }
           }

       }
        return status;
    }




   @Override
    public boolean requiresLogin() {
        return true;
    }
}

