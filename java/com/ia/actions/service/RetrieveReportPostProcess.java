package com.ia.actions.service;

import static com.ia.log.LogUtil.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.ia.insightsStatementUploadBeans.ErrorResponseXml;
import com.ia.log.Logger;
import com.ia.util.DatabaseUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class RetrieveReportPostProcess {

    private final static String filePath = "/perfios/data/";

    private final static String REPORT_FAILED = "REPORT_DELIVERY_FAILED";

    private final static String ERROR_RETRIEVING =
            "Error response receieved while making retrieve report call: [%s]";

    private final static String ERROR = "Error";

    private final XStream xstream = new XStream(new DomDriver());

    private final Logger logger = getLogger(getClass());

    private final DatabaseUtil dataBaseUtil;

    @Inject
    public RetrieveReportPostProcess(final DatabaseUtil databaseUtil) {
        super();
        dataBaseUtil = databaseUtil;
    }

    public void autheticate(final InputStream is, final String clientTransactionId,
            final String extension) throws IOException {
        final String fileName =
                filePath + clientTransactionId + "/" + clientTransactionId + "." + extension;
        if (new File(filePath + clientTransactionId).mkdirs()) {
            FileUtils.writeByteArrayToFile(new File(fileName),
                    org.apache.commons.io.IOUtils.toByteArray(is));

            is.close();
        }
        dataBaseUtil.updateReportLocation(clientTransactionId,
                filePath + clientTransactionId + "/" + clientTransactionId + "." + extension);
    }

    public void logError(final String response, final String clientTransactionId)
            throws IOException {

        if (response.contains(ERROR)) {
            xstream.processAnnotations(ErrorResponseXml.class);
            final ErrorResponseXml errorResponseXml = (ErrorResponseXml) xstream.fromXML(response);
            logger.error(ERROR_RETRIEVING, errorResponseXml.getMessage());
            dataBaseUtil.updateTransactionStatus(clientTransactionId, REPORT_FAILED, errorResponseXml.getMessage());
        }
        else {
            dataBaseUtil.updateTransactionStatus(clientTransactionId, REPORT_FAILED, "error while retrieving the report");
        }
    }

}
