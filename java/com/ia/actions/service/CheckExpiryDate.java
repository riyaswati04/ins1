package com.ia.actions.service;

import static com.ia.log.LogUtil.getLogger;

import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;
import com.ia.log.Logger;
import com.ia.util.DatabaseUtil;

public final class CheckExpiryDate {

	private static Timer checkTransactionExpiryDate;

	private final Logger logger = getLogger(getClass());

	private DatabaseUtil databaseUtil;

	@Inject
	CheckExpiryDate(final DatabaseUtil databaseUtil) {
		this.databaseUtil = databaseUtil;
	}

	public void start() {
		// start() is called by the servlet at startup or after a shutdown.

		// logger is opened/closed as per start()/shutdown()
		// start the logFlusher

		checkTransactionExpiryDate = new Timer("TransactionExpiryDate-checker", true); // daemon
																						// process

		checkTransactionExpiryDate.schedule(new TimerTask() {
			@Override
			public void run() {
				boolean success = databaseUtil.updateExpiryDateStatus();
				logger.debug("invalidate the state of all the perfios transaction ids, success=" + success);
			}
		}, 0L, 1 * 60 * 60 * 1000L); // every 3 mins
	}

	public void shutdown() {
		logger.debug("stopping the job for invalidation of the expired transaction ids");
		checkTransactionExpiryDate.cancel();
	}
}
