package com.ia.actions.service;

import static com.ia.core.util.IAConstants.STATUS_EMAIL_NOT_SENT;
import static com.ia.core.util.IAConstants.STATUS_EMAIL_SEND_MESSAGE;
import static com.ia.core.util.IAConstants.STATUS_INIT_RESPONSE_RECEIVED_MESSAGE;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.services.MailerService.sendMail;
import static com.ia.util.DateUtil.convert;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.ia.core.util.IAConstants;
import com.ia.insightsStatementUploadBeans.ErrorResponseXml;
import com.ia.insightsStatementUploadBeans.GenerateUrlSuccess;
import com.ia.log.Logger;
import com.ia.util.DatabaseUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GenerateLinkPostProcess {

    private static final XStream xstream = new XStream(new DomDriver());

    private final Logger logger = getLogger(getClass());

    private final DatabaseUtil dataBaseUtil;

    private Date expiryDate;

    private final static String ERROR_RETRIEVING =
            "Error response receieved while making retrieve report call: [%s]";

    private final static String ERROR = "Error";

    private final static String EMAIL = "Email has been sent to user";

    private final static String INIT_RESPONSE = "Initial Response recieved";

    @Inject
    public GenerateLinkPostProcess(final DatabaseUtil databaseUtil) {
        super();
        dataBaseUtil = databaseUtil;
    }

    public void autheticate(final String xml, final String clientTransactionId, final int userId,
            final String emailId) {

        if (xml.contains("Success")) {
            try {
                xstream.processAnnotations(GenerateUrlSuccess.class);

                final GenerateUrlSuccess generateUrlSuccess =
                        (GenerateUrlSuccess) xstream.fromXML(xml);

                final String generatedUrl = generateUrlSuccess.getUrl();
                expiryDate = convert(generateUrlSuccess.getExpires());

                dataBaseUtil.updateTransactions(clientTransactionId, null,
                        STATUS_INIT_RESPONSE_RECEIVED_MESSAGE, null, INIT_RESPONSE, 0, generatedUrl,
                        expiryDate);

                final Set<String> emailIdSet = new HashSet<String>();
                emailIdSet.add(emailId);

                logger.debug("sending an email to:" + emailId);

                sendMail(emailIdSet, "Please click the insights link to proceed:" + generatedUrl,
                        "Perfios Insights.");

                dataBaseUtil.updateTransactions(clientTransactionId, null,
                        STATUS_EMAIL_SEND_MESSAGE, null, EMAIL, 0, generatedUrl, expiryDate);
            }
            catch (final ParseException e) {
                logger.debug(e, "Transaction [%s] process occuried error ", clientTransactionId);
            }
            catch (final Exception e) {
                logger.debug(e, "Transaction [%s] process occuried error ", clientTransactionId);
            }

        }
        else if (xml.contains("Error")) {

            dataBaseUtil.updateTransactions(clientTransactionId, null, IAConstants.ERROR, null,
                    IAConstants.ERROR, 0);

        }

        logger.debug("Transaction [%s] status has updated", clientTransactionId);
    }

    public void logError(final String response, final String clientTransactionId,
            final String emailId) throws IOException {

        if (response.contains(ERROR)) {
            xstream.processAnnotations(ErrorResponseXml.class);

            final ErrorResponseXml errorResponseXml = (ErrorResponseXml) xstream.fromXML(response);
            logger.error(ERROR_RETRIEVING, errorResponseXml.getMessage());

            dataBaseUtil.updateTransactions(clientTransactionId, null, STATUS_EMAIL_NOT_SENT, null,
                    errorResponseXml.getMessage(), 0);
        }
        else {
            dataBaseUtil.updateTransactions(clientTransactionId, null, STATUS_EMAIL_NOT_SENT, null,
                    "Unable to generate link", 0);
        }
    }
}
