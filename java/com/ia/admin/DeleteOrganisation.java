package com.ia.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;

import com.ia.actions.AbstractAction;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;


import com.ia.beans.GenericResultBean;
import com.ia.generated.tables.IaOrganisations;
import com.ia.services.impl.OrganisationServiceImpl;
import org.jooq.DSLContext;
import org.jooq.tools.StringUtils;

@Singleton
@ActionPath("/api/v1/deleteOrganisation")

public class DeleteOrganisation extends AbstractAction{
    private final DSLContext dslContext;
    private final OrganisationServiceImpl organisationCache;


    @Inject
    public DeleteOrganisation(final DSLContext dslContext,final OrganisationServiceImpl organisationCache){
        super();
        this.dslContext=dslContext;
        this.organisationCache=organisationCache;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception{
        final GenericResultBean result = new GenericResultBean(false);
        response.setContentType("application/json");
        String organisationName=request.getParameter("deleteOrgName");
        Byte enabled=0;
        if(StringUtils.isEmpty(organisationName)){
            result.setSuccess(false);
            result.setMessage("Organisation name is null");
            sendResponse(response,result);
            return;
        }

        if (deleteOrganisation(organisationName,enabled)) {
            result.setSuccess(true);
            organisationCache.restart();
            sendResponse(response, result);


        } else {
            result.setMessage("error");
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;


        }


    }
    public boolean deleteOrganisation(String organisationName,Byte enabled){
        boolean status=false;

        try{
            dslContext.update(

                    IaOrganisations.IA_ORGANISATIONS)

                    .set(IaOrganisations.IA_ORGANISATIONS.ENABLED,enabled)

                    .where(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME.equal(organisationName))

                    .execute();

            logger.info("updated");

            dslContext.delete(IaOrganisations.IA_ORGANISATIONS)

                    .where(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME.equal(organisationName))

                    .execute();

            logger.info("deleted");

            status= true;

        }catch (Exception e) {
            logger.error(e,"Exception caught");
        }
        return status;

    }
    @Override
    public boolean requiresLogin() {
        return true;
    }

}
