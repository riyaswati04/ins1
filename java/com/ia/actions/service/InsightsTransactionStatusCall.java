package com.ia.actions.service;

import static com.github.rholder.retry.StopStrategies.stopAfterAttempt;
import static com.github.rholder.retry.WaitStrategies.randomWait;
import static com.ia.log.LogUtil.getLogger;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.httpclient.ConnectTimeoutException;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.mashape.unirest.request.HttpRequest;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.ia.log.Logger;
import com.mashape.unirest.http.Unirest;

public class InsightsTransactionStatusCall {

	private final Logger logger = getLogger(getClass());

	private final HandleReturnUrlPostProcess handleReturnUrlPostProcess;

	@Inject
	public InsightsTransactionStatusCall(final HandleReturnUrlPostProcess handleReturnUrlPostProcess) {
		super();
		this.handleReturnUrlPostProcess = handleReturnUrlPostProcess;
	}

	public void executeAsync(final String signature, final String server, final String payload,
			final String clientTransactionId) {

		final Thread callbackThread = new Thread(new Runnable() {
			@Override
			public void run() {
				final Callable<Boolean> callable = new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						try {
							logger.debug("Making call with url=%s, payload=%s", server, payload);

							final HttpRequest httpRequest = Unirest.post(server).header("accept", "text/xml")
									.field("payload", payload).field("signature", signature).getHttpRequest();

							logger.debug("Body %s", httpRequest.asString().getBody());

							if (httpRequest.asString().getStatus() == 200 || httpRequest.asString().getStatus() == 400
									|| httpRequest.asString().getStatus() == 404) {
								handleReturnUrlPostProcess.autheticate(httpRequest.asString().getBody(),
										clientTransactionId);
								return true;
							}

							return false;
						} catch (final Exception e) {
							logger.error("Exception retrying %s", e);
						} finally {
							// Unirest.shutdown();
						}
						return true;
					}

				};

				final Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()

						/* If HTTP response is not 200 */

						.retryIfResult(Predicates.<Boolean>equalTo(false))

						.retryIfExceptionOfType(IOException.class)

						.retryIfExceptionOfType(SocketTimeoutException.class)

						.retryIfExceptionOfType(ConnectTimeoutException.class)

						.retryIfExceptionOfType(ConnectException.class)

						.retryIfExceptionOfType(ExecutionException.class)

						.retryIfRuntimeException()

						.retryIfException()

						/* Attempt for 30 minutes */

						.withStopStrategy(stopAfterAttempt(10))

						/* Random wait between 20 to 60 seconds */

						.withWaitStrategy(randomWait(5, SECONDS, 90, SECONDS))

						.build();

				try {
					final boolean result = retryer.call(callable);
					logger.debug("InsightsTransactionStatusCall result --> " + result);
				} catch (final Exception e) {
					logger.error("InsightsTransactionStatusCall Exception", e, server);
				}
			}
		});

		callbackThread.setDaemon(true);

		callbackThread.start();
	}
}
