package com.ia.services.impl;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.ia.services.SessionService;

public final class ActionServiceImpl extends AbstractActionService {

    @Inject
    public ActionServiceImpl(final Injector injector, final SessionService sessionService) {
        super(injector, sessionService);
    }

    @Override
    protected void registerAllPaths() throws Exception {
        registerPath("/api/v1/adminLogout");

        registerPath("/api/v1/alogin/dashboard_dropdown");
	    registerPath("/api/v1/alogin/dashboard_org");
	    registerPath("/api/v1/alogin/dashboard");
        registerPath("/api/v1/alogin");
        registerPath("/api/v1/login");
        registerPath("/api/v1/logout");
        registerPath("/api/v1/generateUrl");
        registerPath("/api/v1/handleInsightsReturnUrl");
        registerPath("/api/v1/completedReport");
        registerPath("/api/v1/fetchData");
        registerPath("/api/v1/downloadReport");
    }
}

