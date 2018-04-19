package com.ia.admin;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.AbstractAction;
import com.ia.beans.GenericResultBean;
import com.ia.core.annotations.ActionPath;
import com.ia.services.impl.OrganisationServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Singleton
@ActionPath("/api/v1/organisationCacheRefresh")

public class OrganisationCacheRefresh extends AbstractAction {


    private OrganisationServiceImpl organisationCache;


    @Inject
    OrganisationCacheRefresh(OrganisationServiceImpl organisationCache){

        super();
        this.organisationCache = organisationCache;

    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);
        organisationCache.restart();
        result.setSuccess(true);
        sendResponse(response, result);

    }


    @Override
    public boolean requiresLogin() {
        return true;
    }
}