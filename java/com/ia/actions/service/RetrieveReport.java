package com.ia.actions.service;

import static com.ia.common.IAProperties.retrieveReportUrl;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.EncryptUtil.buildPrivateKey;
import static com.ia.util.EncryptUtil.getSignature;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.generated.tables.IaOrganisations;
import com.ia.insightsStatementUploadBeans.RetrievePayload;
import com.ia.log.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Singleton
public class RetrieveReport {

	private static final Logger logger = getLogger(RetrieveReport.class);

	private static final String DIGEST_ALGO = "SHA-1";

	private static final String ENCRYPTION_ALGO = "RSA/ECB/PKCS1Padding";

	private static final String FETCH_INSIGHTS_KEY_FAILED = "Fetching insights key for the organisation [%s] failed";

	private static final XStream xstream = new XStream(new DomDriver());

	private String getRetrievePayloadXml(final String perfiosTransactionId, final String clientTransactionId) {
		xstream.processAnnotations(RetrievePayload.class);
		final RetrievePayload retrievePayload = new RetrievePayload(2.0, perfiosTransactionId, "pdf",
				clientTransactionId, "acme");
		return xstream.toXML(retrievePayload);
	}

	private final DSLContext dslContext;

	private final RetrieveReportCall retrieveReportCall;

	private final String extension = "pdf";

	@Inject
	public RetrieveReport(final DSLContext dslContext, final RetrieveReportCall retrieveReportCall) {
		super();
		this.dslContext = dslContext;
		this.retrieveReportCall = retrieveReportCall;
	}

	public void execute(final String clientTransactionId, final String perfiosTransactionId) throws Exception {

		final String privateKey = getInsightsPrivateKeyForUser("acme");

		String payload = getRetrievePayloadXml(perfiosTransactionId, clientTransactionId);
		payload = payload.replaceAll("\n", "");
		payload = payload.replace(" ", "");

		final String signature = getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), payload);

		retrieveReportCall.executeAsync(signature, retrieveReportUrl, payload, clientTransactionId, extension);

	}

	public String getInsightsPrivateKeyForUser(final String organisationName) {
		String insightsKey = null;
		try {

			final Result<Record> result = dslContext

					.select()

					.from(IaOrganisations.IA_ORGANISATIONS)

					.where(IaOrganisations.IA_ORGANISATIONS.ORGANISATION_NAME.equal(organisationName))

					.fetch();

			if (result.size() != 0) {

				insightsKey = result.get(0).getValue(IaOrganisations.IA_ORGANISATIONS.INSIGHTS_KEY);
			}

		} catch (final Exception e) {
			logger.error(FETCH_INSIGHTS_KEY_FAILED, organisationName);
		}

		return insightsKey;
	}

}
