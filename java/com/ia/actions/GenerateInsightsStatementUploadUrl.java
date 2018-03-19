package com.ia.actions;

import static com.ia.common.IAProperties.generateInsightsForStatementUploadUrl;
import static com.ia.common.IAProperties.returnUrlInsights;
import static com.ia.common.IAProperties.transactioncompleteUrl;
import static com.ia.core.util.IAConstants.CONTENT_TYPE_APPLICATION_XML;
import static com.ia.core.util.IAConstants.STATUS_INITIATED_MESSAGE;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.EncryptUtil.buildPrivateKey;
import static com.ia.util.EncryptUtil.getSignature;
import static com.ia.util.KUtilities.generateRandomString;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.service.MakeInsightsApiRequest;
import com.ia.beans.GenericResultBean;
import com.ia.beans.User;
import com.ia.core.annotations.ActionPath;
import com.ia.core.forms.FormBuilder;
import com.ia.forms.LoanForm;
import com.ia.generated.tables.IaOrganisations;
import com.ia.insightsStatementUploadBeans.InsightsGenerateURLPayload;
import com.ia.log.Logger;
import com.ia.services.SessionService;
import com.ia.util.DatabaseUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Singleton
@ActionPath("/api/v1/generateUrl")
public class GenerateInsightsStatementUploadUrl extends AbstractAction {

    private static final String INVALID_JSON = "INVALID_JSON";

    private static final String LOG_ERROR_MALFORMED_JSON = "Malformed json sent by host ";

    private static final String INVALID_PARAMETER = "INVALID_PARAMETER";

    private static final String LOG_ERROR_INVALID_PARAMETER = "Invalid parameter sent by host";

    private static final String LOG_ERROR_INVALID_FORM = "Invalid form sent";

    private static final String FETCH_INSIGHTS_KEY_ERROR =
            "Fetching insights key for the organisations failed";

    private static final String DIGEST_ALGO = "SHA-1";

    private static final String ENCRYPTION_ALGO = "RSA/ECB/PKCS1Padding";

    private static String server = generateInsightsForStatementUploadUrl;

    private static final XStream xstream = new XStream(new DomDriver());

    private final SessionService service;

    private final FormBuilder formBuilder;

    private final DSLContext dslContext;

    private final DatabaseUtil dataBaseUtil;

    private final MakeInsightsApiRequest apiRequest;

    private final Logger logger = getLogger(getClass());

    @Inject
    public GenerateInsightsStatementUploadUrl(final SessionService service,
            final FormBuilder formBuilder, final DSLContext dslContext,
            final DatabaseUtil dataBaseUtil, final MakeInsightsApiRequest apiRequest) {
        super();
        this.formBuilder = formBuilder;
        this.service = service;
        this.dslContext = dslContext;
        this.dataBaseUtil = dataBaseUtil;
        this.apiRequest = apiRequest;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        response.setContentType(CONTENT_TYPE_APPLICATION_XML);

        final GenericResultBean result = new GenericResultBean(false);

        final User user = service.getUser(request.getSession().getId());

        if (user == null) {
            result.setSuccess(false);
            result.setMessage("User null");
            sendResponse(response, result);
            return;
        }

        final Class<LoanForm> formClass = LoanForm.class;

        LoanForm form = null;

        try {
            form = formBuilder.fromJson(formClass, null, request);
        }
        catch (final Exception e) {
            logger.error(e, LOG_ERROR_MALFORMED_JSON);
            result.setMessage(INVALID_JSON);
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        /* Empty */
        if (form == null) {
            logger.error(LOG_ERROR_INVALID_PARAMETER);
            result.setMessage(INVALID_PARAMETER);
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        /* Invalid form */
        if (!form.validate()) {
            logger.error(LOG_ERROR_INVALID_FORM);
            result.setMessage(form.getFirstErrorMessage());
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        final String organisation = form.getOrganisation();

        final String clientTransactionId = generateRandomString(10);

        final String privateKey = getInsightsPrivateKeyForUser(organisation);

        final String returnURL = returnUrlInsights + clientTransactionId;

        final String formDataJson = request.getParameter("json");

        logger.debug(
                "LOG_REQUEST_INFO for clientTransactionId= [%s], organisation= [%s], LoanAmount= [%s], "
                        + "LoanDuration= [%s], LoanType= [%s]," + "returnURL= [%s],"
                        + "email : [%s]",
                clientTransactionId, organisation, form.getLoanAmount(), form.getLoanDuration(),
                form.getLoanType(), returnURL, form.getEmailId());

        String payload = getInsightsPayload(organisation, clientTransactionId, form.getLoanAmount(),
                form.getLoanDuration(), form.getLoanType(), returnURL);

        payload = payload.replaceAll("\n", "");
        payload = payload.replace(" ", "");

        final String signature =
                getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), payload);

        dataBaseUtil.insertIntoTransactions(clientTransactionId, user.getUserId(),
                STATUS_INITIATED_MESSAGE, formDataJson, 0, "Initial Request sent");

        apiRequest.executeAsync(signature, server, payload, clientTransactionId, user.getUserId(),
                form.getEmailId());

        result.setMessage("EMAIL_SENT");
        result.setSuccess(true);

        sendResponse(response, result);

    }

    public String getInsightsPrivateKeyForUser(final String organisationName) {
        String insightsKey = null;
        try {
            final Result<Record> result = dslContext

                    .select()

                    .from(IaOrganisations.IA_ORGANISATIONS)

                    .where(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME
                            .equal(organisationName))

                    .fetch();

            if (result.size() != 0) {
                insightsKey = result.get(0).getValue(IaOrganisations.IA_ORGANISATIONS.INSIGHTS_KEY);
            }
        }
        catch (final Exception e) {
            logger.error(FETCH_INSIGHTS_KEY_ERROR);
        }
        return insightsKey;
    }

    public String getInsightsPayload(final String vendor, final String clientTransactionId,
            final int loanAmount, final int loanDuaration, final String loanType,
            final String returnURL) throws Exception {

        /* Default values passed in Payload for from and to dates. */
        /*TODO: 
         * 1. Add support for netbanking fetch.
         * 2. Customize date range.
         * */

        final InsightsGenerateURLPayload insightsGenerateUrlPayload =
                new InsightsGenerateURLPayload(2.0, vendor, clientTransactionId, "statement",
                        loanAmount, loanDuaration, loanType, "atLeastOneTransactionInRange",
                        returnURL, transactioncompleteUrl + "/acme", "2017-01", "2018-01");

        xstream.processAnnotations(InsightsGenerateURLPayload.class);

        final String xml = xstream.toXML(insightsGenerateUrlPayload);

        return xml;
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

}
