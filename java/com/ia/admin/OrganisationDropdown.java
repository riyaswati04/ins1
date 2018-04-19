package com.ia.admin;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.ia.beans.OrgBean;
import com.ia.beans.OrganisationDropdownBean;
import com.ia.beans.User;
import com.ia.generated.tables.IaOrganisations;
import com.ia.log.Logger;
import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;
import com.ia.actions.AbstractAction;


import static com.ia.log.LogUtil.getLogger;

import java.sql.SQLException;
import com.ia.util.AdminDatabaseUtil;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;


@Singleton
@ActionPath("/api/v1/organisationDropDown")

public class OrganisationDropdown extends AbstractAction {

   public List<OrganisationDropdownBean> organisationList = new ArrayList<OrganisationDropdownBean>();

    private final DSLContext dslContext;
    private final Logger logger = getLogger(getClass());



    @Inject
    public OrganisationDropdown(final DSLContext dslContext){
        super();
        this.dslContext=dslContext;
    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        getOrganisationDetails();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(new Gson().toJson(organisationList));
        out.close();

    }

    public void getOrganisationDetails() {
        try{
            organisationList = dslContext

                    .select(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_ID,IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME)

                    .from(IaOrganisations.IA_ORGANISATIONS)

                    .fetch()

                    .into(OrganisationDropdownBean.class);

        }  catch (Exception e) {
            logger.error(e,"Exception caught");
        }


    }


    @Override
    public boolean requiresLogin() {
        return true;
    }
}
