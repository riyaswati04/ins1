package com.ia.admin;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.AbstractAction;
import com.ia.beans.GenericResultBean;
import com.ia.beans.OrgBean;
import com.ia.core.annotations.ActionPath;
import com.ia.generated.tables.IaOrganisations;
import com.ia.log.Logger;
import com.ia.services.impl.OrganisationServiceImpl;
import com.ia.util.AdminDatabaseUtil;
import com.ia.util.DateUtil;
import org.jooq.DSLContext;
import static java.lang.System.currentTimeMillis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

import static com.ia.log.LogUtil.getLogger;
import static org.jooq.tools.StringUtils.*;

@Singleton
@ActionPath("/api/v1/importOrganisation")

public class ImportOrganisation extends AbstractAction {

    private final Logger logger = getLogger(getClass());
    Gson gson=new Gson();
    boolean status = false;
    final GenericResultBean result = new GenericResultBean(false);
    private final DSLContext dslContext;
    private final AdminDatabaseUtil dbConnection;
    private final OrganisationServiceImpl organisationCache;

    @Inject
    public ImportOrganisation(final AdminDatabaseUtil dbConnection,final OrganisationServiceImpl organisationCache,final DSLContext dslContext){
        super();
        this.dbConnection=dbConnection;
        this.dslContext=dslContext;
        this.organisationCache=organisationCache;
    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        String payloadRequest = getBody(request);

        int dotIndex = payloadRequest.indexOf(".");
        String filetype = payloadRequest.substring(dotIndex+1,dotIndex+5);
        if(!(filetype.equalsIgnoreCase("json")))
        {
            result.setSuccess(false);
            result.setMessage("File should be of type json");
            sendResponse(response, result);
            return;

        }

        if(!(payloadRequest.contains("[")&&payloadRequest.contains("]"))){
            result.setSuccess(false);
            result.setMessage("Invalid file content");
            sendResponse(response, result);
            return;

        }

        int i =payloadRequest.indexOf('[');
        int j =payloadRequest.indexOf(']');
        String payload = payloadRequest.substring(i+1,j);
        logger.info("payload request"+payloadRequest);
        response.setContentType("application/json");
        logger.info("payload"+payload);


        if(isEmpty(payload)){
            result.setSuccess(false);
            result.setMessage("File is empty");
            sendResponse(response, result);
            return;

        }
        uploadOrganisationDetails(payload,response);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        if(status) {
            result.setSuccess(true);
            organisationCache.restart();
            result.setMessage("Organisation added successfully");
            sendResponse(response, result);
        }

    }
    public void uploadOrganisationDetails(String payload,final HttpServletResponse response) {

        OrgBean bean = gson.fromJson(payload, OrgBean.class);
        String name = bean.getorganisation_name();
        String key = bean.getkey();
        String insightsKey = bean.getinsights_key();
        int enabled = bean.getenabled();
        String updated = bean.getupdated();
        String created = bean.getcreated();
        String license = bean.getlicense_end_date();
        PreparedStatement pst = null;

        if (isEmpty(name) || isEmpty(key) || isEmpty(insightsKey) || (enabled == 0) || isEmpty(updated) || isEmpty(created) || isEmpty(license)) {
            result.setSuccess(false);
            result.setMessage("A field is null");
            sendResponse(response, result);
            return;

        }

            try {
                dbConnection.connectDatabase();
                String query = "insert into ia_organisations(organisation_name,`key`,insights_key,enabled,updated,created,license_end_date) values('" + name + "','" + key + "','" + insightsKey + "'," + enabled + ",'" + updated + "','" + created + "','" + license + "')";
                pst = dbConnection.conn.prepareStatement(query);
                pst.executeUpdate();
                status = true;
            } catch (Exception e) {
                logger.error(e, "Exception caught");
                result.setSuccess(false);
                result.setMessage("something went wrong");
                sendResponse(response, result);

            } finally {
                if (dbConnection.conn != null) {
                    try {
                        dbConnection.conn.close();
                    } catch (SQLException e) {
                        logger.error(e, "SQL Exception caught");

                    }
                }
                if (pst != null) {
                    try {
                        pst.close();
                    } catch (SQLException e) {
                        logger.error(e, "Exception caught ");

                    }
                }
               }
      /*  try {
           // SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            //Date licenseEndDate=df.parse(license);
            Date licenseEndDate = (Date) DateUtil.convert(license);
            dslContext.insertInto(IaOrganisations.IA_ORGANISATIONS,

                    IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME,

                    IaOrganisations.IA_ORGANISATIONS.KEY,

                    IaOrganisations.IA_ORGANISATIONS.INSIGHTS_KEY,

                    IaOrganisations.IA_ORGANISATIONS.ENABLED,

                    IaOrganisations.IA_ORGANISATIONS.UPDATED,

                    IaOrganisations.IA_ORGANISATIONS.CREATED,

                    IaOrganisations.IA_ORGANISATIONS.LICENSE_END_DATE)

                    .values(

                            name,

                            key,

                            insightsKey,

                            (byte) enabled,

                            new Timestamp(currentTimeMillis()),


                            new Timestamp(currentTimeMillis()),


                           licenseEndDate)

                    .execute();

            status=true;
        } catch (Exception e) {
            logger.error(e, "Exception caught");
        }*/


    }
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }
}