package com.ia.util;

import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.JsonUtil.toJson;
import static java.lang.System.currentTimeMillis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.ResultQuery;

import com.google.inject.Inject;
import com.ia.beans.UserData;
import com.ia.generated.tables.IaTransactions;
import com.ia.log.Logger;

public class DatabaseUtil {

	private static final String LOG_ERROR_UPDATING_TRANSACTION_STATUS = "Error updating transaction status [%s] token [%s]";

	private static final String LOG_EXCEPTION_COULD_NOT_RECORD_TRANSACTION = "Could not store to database for token=[%s]";

	private static final String LOG_INFO_UPDATE_TRANSACTION_STATUS = "Update AUA transaction status for token=%s, status=%s";

	private static final String LOG_INFO_UPDATE_REPORT_LOCATION = "Update report path for token=%s, report location=%s";

	private static final String LOG_ERROR_UPDATING_DIR = "Error updating dir token [%s]";

	private static final String LOG_ERROR_UPDATING_STATUS = "Error updating the status [%s]";

	private static final String LOG_ERROR_FETCH = "Error during fetch the data. userId=[%s]";

	private final Logger logger = getLogger(getClass());

	private final DSLContext dslContext;

	@Inject
	public DatabaseUtil(final DSLContext dslContext) {
		super();
		this.dslContext = dslContext;
	}

	public String fetchReportLocation(final String clientTransactionId) {

		String reportLocation = null;

		logger.info(LOG_INFO_UPDATE_REPORT_LOCATION, clientTransactionId, reportLocation);

		try {

			final ResultQuery<Record1<String>> query = dslContext

					.select(IaTransactions.IA_TRANSACTIONS.REPORT_LOCATION)

					.from(IaTransactions.IA_TRANSACTIONS)

					.where(IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID.eq(clientTransactionId));

			reportLocation = dslContext.fetchValue(query);

		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_FETCH, clientTransactionId);
		}

		return reportLocation;
	}

	public String getUserData(final int userId) {
		try {

			List<UserData> userTransactions = new ArrayList<UserData>();

			userTransactions = dslContext

					.selectFrom(IaTransactions.IA_TRANSACTIONS)

					.where(IaTransactions.IA_TRANSACTIONS.USER_ID.eq(userId)).fetch()

					.into(UserData.class);

			final String message = toJson(userTransactions);

			return message;
		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_FETCH, userId);
		}
		return null;
	}

	public boolean insertIntoTransactions(final String clientTransactionId, final int userId, final String status,
			String formDataJson, final int isDeleted, final String message) {

		boolean success = false;

		try {
			dslContext.insertInto(IaTransactions.IA_TRANSACTIONS,

					IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID,

					IaTransactions.IA_TRANSACTIONS.USER_ID,

					IaTransactions.IA_TRANSACTIONS.STATUS,

					IaTransactions.IA_TRANSACTIONS.FORM_DATA_JSON,

					IaTransactions.IA_TRANSACTIONS.MESSAGE,

					IaTransactions.IA_TRANSACTIONS.IS_DELETED,

					IaTransactions.IA_TRANSACTIONS.CREATED)

					.values(

							clientTransactionId,

							userId,

							status,

							formDataJson,

							message,

							(byte) isDeleted,

							new Timestamp(currentTimeMillis()))

					.execute();

			success = true;
		} catch (final Exception e) {
			logger.exception(LOG_EXCEPTION_COULD_NOT_RECORD_TRANSACTION, e, clientTransactionId);
		}

		return success;
	}

	public boolean updateReportLocation(final String clientTransactionId, final String reportLocation) {

		boolean success = false;

		logger.info(LOG_INFO_UPDATE_REPORT_LOCATION, clientTransactionId, reportLocation);

		try {
			dslContext.update(

					IaTransactions.IA_TRANSACTIONS)

					.set(IaTransactions.IA_TRANSACTIONS.REPORT_LOCATION, reportLocation)

					.where(IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID.eq(clientTransactionId))

					.execute();

			success = true;
		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_UPDATING_DIR, clientTransactionId);
		}

		return success;
	}

	public boolean updateTransactions(final String clientTransactionId, final String perfiosTransactionId,
			final String status, final String insightsStatus, final String message, final int isDeleted) {

		boolean success = false;

		logger.info(LOG_INFO_UPDATE_TRANSACTION_STATUS, clientTransactionId, status);

		try {
			dslContext.update(

					IaTransactions.IA_TRANSACTIONS)

					.set(IaTransactions.IA_TRANSACTIONS.PERFIOS_TRANSCATION_ID, perfiosTransactionId)

					.set(IaTransactions.IA_TRANSACTIONS.STATUS, status)

					.set(IaTransactions.IA_TRANSACTIONS.INSIGHTS_STATUS, insightsStatus)

					.set(IaTransactions.IA_TRANSACTIONS.IS_DELETED, (byte) isDeleted)

					.set(IaTransactions.IA_TRANSACTIONS.MESSAGE, message)

					.where(IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID.eq(clientTransactionId))

					.execute();

			success = true;
		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_UPDATING_TRANSACTION_STATUS, status, clientTransactionId);
		}

		return success;

	}

	public boolean updateTransactions(final String clientTransactionId, final String perfiosTransactionId,
			final String status, final String insightsStatus, final String message, final int isDeleted,
			final String generatedUrl, final Date expiryDate) {

		boolean success = false;

		final Timestamp time = new Timestamp(expiryDate.getTime());

		logger.info(LOG_INFO_UPDATE_TRANSACTION_STATUS, clientTransactionId, status);

		try {
			dslContext.update(

					IaTransactions.IA_TRANSACTIONS)

					.set(IaTransactions.IA_TRANSACTIONS.PERFIOS_TRANSCATION_ID, perfiosTransactionId)

					.set(IaTransactions.IA_TRANSACTIONS.STATUS, status)

					.set(IaTransactions.IA_TRANSACTIONS.INSIGHTS_STATUS, insightsStatus)

					.set(IaTransactions.IA_TRANSACTIONS.IS_DELETED, (byte) isDeleted)

					.set(IaTransactions.IA_TRANSACTIONS.MESSAGE, message)

					.set(IaTransactions.IA_TRANSACTIONS.GENERATED_LINK, generatedUrl)

					.set(IaTransactions.IA_TRANSACTIONS.EXPIRY_DATE, time)

					.where(IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID.eq(clientTransactionId))

					.execute();

			success = true;
		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_UPDATING_TRANSACTION_STATUS, status, clientTransactionId);
		}

		return success;

	}

	public boolean updateTransactionStatus(final String clientTransactionId, final String status, final String message) {

		boolean success = false;

		try {
			dslContext.update(

					IaTransactions.IA_TRANSACTIONS)

					.set(IaTransactions.IA_TRANSACTIONS.STATUS, status)
					
					.set(IaTransactions.IA_TRANSACTIONS.MESSAGE, message)

					.where(IaTransactions.IA_TRANSACTIONS.TRANSACTION_ID.eq(clientTransactionId))

					.execute();

			success = true;
		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_UPDATING_STATUS, clientTransactionId);
		}

		return success;
	}

	public boolean updateExpiryDateStatus() {

		boolean success = false;
		Calendar cal = Calendar.getInstance();

		try {
			dslContext.update(IaTransactions.IA_TRANSACTIONS).set(IaTransactions.IA_TRANSACTIONS.STATUS, "EXPIRED")
					.set(IaTransactions.IA_TRANSACTIONS.MESSAGE, "The transaction has expired")
					.where(IaTransactions.IA_TRANSACTIONS.EXPIRY_DATE.le(new Timestamp(cal.getTimeInMillis())))
					.execute();

			success = true;

		} catch (final Exception e) {
			logger.error(e, LOG_ERROR_UPDATING_STATUS);
		}
		return success;

	}

}
