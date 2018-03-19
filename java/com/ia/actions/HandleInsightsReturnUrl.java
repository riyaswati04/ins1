package com.ia.actions;

import static com.ia.common.IAProperties.getInsightsTransactionStatus;
import static com.ia.core.util.IAConstants.CONTENT_TYPE_APPLICATION_JSON;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.EncryptUtil.buildPrivateKey;
import static com.ia.util.EncryptUtil.getSignature;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.service.InsightsTransactionStatusCall;
import com.ia.beans.GenericResultBean;
import com.ia.core.annotations.ActionPath;
import com.ia.generated.tables.IaOrganisations;
import com.ia.insightsStatementUploadBeans.TransactionStatusPayload;
import com.ia.log.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Singleton
@ActionPath("/api/v1/handleInsightsReturnUrl")
public class HandleInsightsReturnUrl extends AbstractAction {

	private static final String INVALID_PARAMETER = "INVALID_PARAMETER";

	private static final String LOG_ERROR_INVALID_PARAMETER = "Invalid parameter sent by host";

	private static final Logger logger = getLogger(HandleInsightsReturnUrl.class);

	private static String server = getInsightsTransactionStatus;

	private static final String DIGEST_ALGO = "SHA-1";

	private static final String ENCRYPTION_ALGO = "RSA/ECB/PKCS1Padding";

	private static final String FETCH_INSIGHTS_KEY_ERROR = "Fetching insights key for the organisations failed";

	private static final XStream xstream = new XStream(new DomDriver());

	public String getTransactionStatusPayload(final String vendor, final String clientTransactionId) throws Exception {

		final TransactionStatusPayload transactionStatusPayload = new TransactionStatusPayload(2.0, clientTransactionId,
				vendor);

		xstream.processAnnotations(TransactionStatusPayload.class);

		final String xml = xstream.toXML(transactionStatusPayload);

		return xml;
	}

	private final DSLContext dslContext;

	private final InsightsTransactionStatusCall insightsTransactionStatausCall;

	@Inject
	public HandleInsightsReturnUrl(final DSLContext dslContext,
			final InsightsTransactionStatusCall insightsTransactionStatausCall) {
		super();
		this.dslContext = dslContext;
		this.insightsTransactionStatausCall = insightsTransactionStatausCall;
	}

	@Override
	public void execute(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		response.setContentType(CONTENT_TYPE_APPLICATION_JSON);

		final String clientTransactionId = request.getParameter("id");

		final GenericResultBean result = new GenericResultBean(false);

		final String vendor = "acme";

		logger.debug("Insights Return URL Received. txnId = [%s]", clientTransactionId);

		if (clientTransactionId == null) {
			logger.error(LOG_ERROR_INVALID_PARAMETER);
			result.setMessage(INVALID_PARAMETER);
			response.setStatus(SC_BAD_REQUEST);
			sendResponse(response, result);
			return;
		}

		final String privateKey = getInsightsPrivateKeyForUser(vendor);

		String payload = getTransactionStatusPayload(vendor, clientTransactionId);
		payload = payload.replaceAll("\n", "");
		payload = payload.replace(" ", "");

		final String signature = getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), payload);

		insightsTransactionStatausCall.executeAsync(signature, server, payload, clientTransactionId);

		result.setMessage("Success");

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

		} catch (final Exception e) {
			logger.error(FETCH_INSIGHTS_KEY_ERROR);
		}

		return insightsKey;
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}
}
