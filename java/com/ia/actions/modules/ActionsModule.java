package com.ia.actions.modules;

import static com.google.inject.name.Names.named;

import com.google.inject.AbstractModule;
import com.ia.actions.Action;
import com.ia.actions.DownloadReport;
import com.ia.actions.FetchUserData;
import com.ia.actions.GenerateInsightsStatementUploadUrl;
import com.ia.actions.HandleInsightsReturnUrl;
import com.ia.actions.LoginAuthenticate;
import com.ia.actions.Logout;
import com.ia.actions.TransactionCallBackComplete;
import com.ia.admin.*;

public class ActionsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Action.class).annotatedWith(named("/api/v1/adminLogout")).to(LogOut.class);
        bind(Action.class).annotatedWith(named("/api/v1/alogin/dashboard_dropdown")).to(OrganisationFilter.class);
        bind(Action.class).annotatedWith(named("/api/v1/alogin/dashboard_org")).to(StoreOrganisation.class);
        bind(Action.class).annotatedWith(named("/api/v1/alogin/dashboard")).to(StoreUserData.class);
        bind(Action.class).annotatedWith(named("/api/v1/alogin")).to(adminlogin.class);
        bind(Action.class).annotatedWith(named("/api/v1/login")).to(LoginAuthenticate.class);

        bind(Action.class).annotatedWith(named("/api/v1/logout")).to(Logout.class);

        bind(Action.class).annotatedWith(named("/api/v1/generateUrl"))
                .to(GenerateInsightsStatementUploadUrl.class);

        bind(Action.class).annotatedWith(named("/api/v1/handleInsightsReturnUrl"))
                .to(HandleInsightsReturnUrl.class);

        bind(Action.class).annotatedWith(named("/api/v1/completedReport"))
                .to(TransactionCallBackComplete.class);

        bind(Action.class).annotatedWith(named("/api/v1/fetchData")).to(FetchUserData.class);

        bind(Action.class).annotatedWith(named("/api/v1/downloadReport")).to(DownloadReport.class);
    }
}

