package com.ia.common;

import java.util.ResourceBundle;

/**
 * This class is mirror of ia.properties. This holds all property value in-memory loaded at servlet
 * start-up.
 *
 * NOTE: Do not store passwords after they are consumed.
 */
public class IAProperties {

    public static String authKey;

    public static int autofetchTimeoutLimit;

    public static String bashCommand;

    private static ResourceBundle iaProperties;

    public static String logDirProp;

    public static String logFileProp;

    public static String perlCommand;

    public static boolean productionSystem = false;

    public static String pythonCommand;

    public static boolean servletDebug = false;

    public static String templateBaseDir;

    public static String mfaURL;

    public static String emailRefreshToken;

    public static String supportEmailId;

    public static String machineName;

    public static String tempFilesRootDir;

    public static String secretKey;

    public static String generateInsightsForStatementUploadUrl;

    public static String getInsightsTransactionStatus;

    public static String transactioncompleteUrl;

    public static String returnUrlInsights;

    public static String retrieveReportUrl;

    public static String getInsightsInstitutions;

    public static String getProperty(final String name) {
        return iaProperties.getString(name);
    }

    public static void load(final ResourceBundle props) {
        iaProperties = props;
        final String reqDebug = iaProperties.getString("servlet.debug");
        servletDebug = "true".equals(reqDebug);
        final String prodSystem = iaProperties.getString("production.system");
        productionSystem = "true".equals(prodSystem);
        logDirProp = iaProperties.getString("log.dir");
        logFileProp = iaProperties.getString("log.file");
        perlCommand = iaProperties.getString("perl.command");
        pythonCommand = iaProperties.getString("python.command");
        bashCommand = iaProperties.getString("bash.command");
        authKey = iaProperties.getString("auth.key");
        templateBaseDir = iaProperties.getString("template.base.dir");
        tempFilesRootDir = iaProperties.getString("tempFiles.rootDir");
        emailRefreshToken = iaProperties.getString("email.refresh.token");
        supportEmailId = iaProperties.getString("support.email.id");
        machineName = iaProperties.getString("machine.name");
        tempFilesRootDir = iaProperties.getString("tempFiles.rootDir");
        secretKey = iaProperties.getString("crypt.secret.key");
        generateInsightsForStatementUploadUrl = iaProperties.getString("ia.generate.insights");
        getInsightsTransactionStatus = iaProperties.getString("ia.transaction.status");
        transactioncompleteUrl = iaProperties.getString("ia.transaction.callback.url");
        returnUrlInsights = iaProperties.getString("return.url");
        retrieveReportUrl = iaProperties.getString("ia.retrieve.report");
        getInsightsInstitutions = iaProperties.getString("ia.get.institutions");
    }
}
