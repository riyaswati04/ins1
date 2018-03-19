package com.ia.actions.paths.impl;

import static com.ia.enums.HTTP_METHOD.GET;
import static com.ia.enums.HTTP_METHOD.POST;
import static com.ia.enums.MODE.IA;
import static com.ia.enums.MODE.NONE;

import javax.inject.Inject;

public final class PathsImpl extends AbstractPathsImpl {

    @Inject
    public PathsImpl() {
        super();
        setHTTPMethod("/api/v1/login", POST);
        setPermission("/api/v1/login", IA);

        setHTTPMethod("/api/v1/logout", POST);
        setPermission("/api/v1/logout", IA);

        setHTTPMethod("/api/v1/generateUrl", POST);
        setPermission("/api/v1/generateUrl", IA);

        setHTTPMethod("/api/v1/handleInsightsReturnUrl", POST);
        setPermission("/api/v1/handleInsightsReturnUrl", IA);

        setHTTPMethod("/api/v1/completedReport", POST);
        setPermission("/api/v1/completedReport", IA);

        setHTTPMethod("/api/v1/fetchData", POST);
        setPermission("/api/v1/fetchData", IA);

        setHTTPMethod("/api/v1/downloadReport", GET);
        // setPermission("/api/v1/downloadReport", IA);

        setHTTPMethod("/api/v1/alogin", POST);
        setPermission("/api/v1/alogin", NONE);

        setHTTPMethod("/api/v1/alogin/dashboard", POST);
        setPermission("/api/v1/alogin/dashboard", NONE);

        setHTTPMethod("/api/v1/alogin/dashboard_org", POST);
        setPermission("/api/v1/alogin/dashboard_org",NONE);

        setHTTPMethod("/api/v1/alogin/dashboard_dropdown", POST);
        setPermission("/api/v1/alogin/dashboard_dropdown", NONE);

        setHTTPMethod("/api/v1/adminLogout", POST);
        setPermission("/api/v1/adminLogout", NONE);





    }
}
