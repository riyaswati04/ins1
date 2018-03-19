package com.ia.actions;

import static com.ia.log.LogUtil.getLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.service.RetrieveReport;
import com.ia.core.annotations.ActionPath;
import com.ia.log.Logger;

@Singleton
@ActionPath("/api/v1/completedReport")
public class TransactionCallBackComplete extends AbstractAction {

    private final Logger logger = getLogger(getClass());

    private final RetrieveReport retriveReport;

    @Inject
    public TransactionCallBackComplete(final RetrieveReport retriveReport) {
        super();
        this.retriveReport = retriveReport;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final String clientTransactionId = request.getParameter("clientTransactionId");

        final String perfiosTransactionId = request.getParameter("perfiosTransactionId");

        logger.debug(
                "CompletedReport request for clientTransactionId = [%s], perfiosTransactionId = [%s]",
                clientTransactionId, perfiosTransactionId);

        retriveReport.execute(clientTransactionId, perfiosTransactionId);
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

}
