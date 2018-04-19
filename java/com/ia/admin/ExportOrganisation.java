package com.ia.admin;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.ia.beans.GenericResultBean;
import com.ia.beans.OrgBean;
import com.ia.generated.tables.IaOrganisations;
import com.ia.log.Logger;
import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;
import com.ia.actions.AbstractAction;
import java.io.FileWriter;



import static com.ia.log.LogUtil.getLogger;

import com.ia.util.TempFileUtil;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Singleton

@ActionPath("/api/v1/exportOrganisation")

public class ExportOrganisation extends AbstractAction {

    final GenericResultBean result = new GenericResultBean(false);
    private final DSLContext dslContext;
    public List<OrgBean> organisationList = new ArrayList<OrgBean>();

    private final Logger logger = getLogger(getClass());
    public File fileName;


    @Inject
    public ExportOrganisation(final DSLContext dslContext){
        super();
        this.dslContext = dslContext;
    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        String name=request.getParameter("organization");
        if(StringUtils.isEmpty(name)){
            result.setSuccess(false);
            result.setMessage("Organisation name is null");
            sendResponse(response,result);
            return;
        }

        retreiveOrganisationDetails(name);
        String filePath = fileName +"/"+name+".json";
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);


        response.setContentType("application/octet-stream");
        response.setContentLength((int) downloadFile.length());
        response.setStatus(200);
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();




    }
    public void retreiveOrganisationDetails(String name) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        Gson gson =new Gson();
            try{
                organisationList = dslContext

                        .selectFrom(IaOrganisations.IA_ORGANISATIONS)

                        .where(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME.eq(name)).fetch()

                        .into(OrgBean.class);

                String jsonInString = gson.toJson(organisationList);
                fileName =TempFileUtil.createTempDir();
                try (FileWriter file = new FileWriter(fileName+"/"+name+".json")) {
                    file.write(jsonInString);
                    file.flush();
                }

        }  catch (Exception e) {
            logger.error(e,"Exception caught");
        }
    }


    @Override
    public boolean requiresLogin() {
        return true;
    }
}