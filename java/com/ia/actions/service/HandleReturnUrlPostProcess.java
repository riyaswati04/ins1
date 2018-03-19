package com.ia.actions.service;

import static com.ia.log.LogUtil.getLogger;

import com.google.inject.Inject;
import com.ia.insightsStatementUploadBeans.ErrorResponseXml;
import com.ia.insightsStatementUploadBeans.TransactionStatusResponseXml;
import com.ia.log.Logger;
import com.ia.util.DatabaseUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class HandleReturnUrlPostProcess {

    private final static String LOG_XML_DATA =
            "Xml data clientTransactionId = [%s], processing = [%s], files = [%s], status = [%s], perfiosTransactionId = [%s]";

    private final static String LOG_RETRIEVE_CALL =
            "Making retrieve report call from transaction status handler for clientTransactionId [%s], perfiosTransactionId [%s]";

    private final XStream xstream = new XStream(new DomDriver());

    private final Logger logger = getLogger(getClass());

    private final DatabaseUtil dataBaseUtil;

    private final RetrieveReport retrieveReport;

    @Inject
    public HandleReturnUrlPostProcess(final DatabaseUtil databaseUtil,
            final RetrieveReport retrieveReport) {
        super();
        dataBaseUtil = databaseUtil;
        this.retrieveReport = retrieveReport;
    }

    public void autheticate(final String xml, final String clientTransactionId) throws Exception {

        logger.debug("Insights xml response for clientTransactionId [%s] is [%s]",
                clientTransactionId, xml);

        if (xml != null && xml.contains("Status")) {
            xstream.processAnnotations(TransactionStatusResponseXml.class);

            final TransactionStatusResponseXml transactionStatusResponseXml =
                    (TransactionStatusResponseXml) xstream.fromXML(xml);

            checkFilesAvailable(transactionStatusResponseXml, clientTransactionId);
        }
        else if (xml != null && xml.contains("Error")) {
            xstream.processAnnotations(ErrorResponseXml.class);

            final ErrorResponseXml errorResponseXml = (ErrorResponseXml) xstream.fromXML(xml);
            // TODO need to update perfios txn id
            dataBaseUtil.updateTransactions(clientTransactionId, null, "ERROR",
                    errorResponseXml.getCode(), errorResponseXml.getMessage(), 0);
        }
        else {
        	logger.error("error getting the transaction status");
        	dataBaseUtil.updateTransactionStatus(clientTransactionId, "ERROR", "Error while retrieving the transaction status.");
        }
    }

    public boolean checkFilesAvailable(
            final TransactionStatusResponseXml transactionStatusResponseXml,
            final String clientTransactionId) throws Exception {

        final String processing = transactionStatusResponseXml.getProcessing();
        final String files = transactionStatusResponseXml.getFiles();
        final String status = transactionStatusResponseXml.getPart().getStatus();
        final String perfiosTransactionId = transactionStatusResponseXml.getPart().getPerfTxnId();

        logger.debug(LOG_XML_DATA, clientTransactionId, processing, files, status,
                perfiosTransactionId);

        if (status.equals("success") && processing.equals("completed")
                && files.equals("available")) {

            final String message = "Completed, files can be retrieved";
            final String insightsStatus = "COMPLETED";
            final String insightsAssistStatus = "COMPLETED";

            dataBaseUtil.updateTransactions(clientTransactionId, perfiosTransactionId,
                    insightsAssistStatus, insightsStatus, message, 0);

            logger.debug(LOG_RETRIEVE_CALL, clientTransactionId, perfiosTransactionId);

            retrieveReport.execute(clientTransactionId, perfiosTransactionId);

            return true;
        }
        else if (status.equals("success") && processing.equals("pending")) {

            final String message = "processing is pending";
            final String insightsStatus = "pending";
            final String insightsAssistStatus = "PROCESSING";

            dataBaseUtil.updateTransactions(clientTransactionId, perfiosTransactionId,
                    insightsAssistStatus, insightsStatus, message, 0);

            return true;
        }
        else if (status.equals("failure")) {

            final String message = transactionStatusResponseXml.getPart().getReason();
            final String insightsStatus = "failure";
            final String insightsAssistStatus = "ERROR";

            dataBaseUtil.updateTransactions(clientTransactionId, perfiosTransactionId,
                    insightsAssistStatus, insightsStatus, message, 0);

            logger.debug("extracting failure message and return it");

            return false;
        }
        else if (status.equals("deleted")) {

            final String message = "transaction has been deleted.";
            final String insightsStatus = "deleted";
            final String insightsAssistStatus = "COMPLETED";

            dataBaseUtil.updateTransactions(clientTransactionId, perfiosTransactionId,
                    insightsAssistStatus, insightsStatus, message, 1);

            return true;
        }
        else {

            final String message = "Error";
            final String insightsStatus = "Error";
            final String insightsAssistStatus = "ERROR";

            dataBaseUtil.updateTransactions(clientTransactionId, perfiosTransactionId,
                    insightsAssistStatus, insightsStatus, message, 1);

            return false;
        }

    }

}
