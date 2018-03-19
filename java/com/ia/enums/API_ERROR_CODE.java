package com.ia.enums;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;

public enum API_ERROR_CODE {

    AccessDenied(SC_FORBIDDEN, "Client is not permitted to access this URL"),

    ArtifactNotFound(SC_NOT_FOUND,
            "We could not find the transaction artifact referred to by the client"),

    BadParameter(SC_BAD_REQUEST, "One or more parameters sent by the Client are wrong"),

    CannotGenerateReport(SC_BAD_REQUEST,
            "We could not generate a report. Use the transaction status API to determine why"),

    InternalError(SC_INTERNAL_SERVER_ERROR, "We encountered an internal error. Please try again."),

    InvalidDOB(SC_OK, "Invalid DOB provided by client."),

    InvalidAccountNumber(SC_OK, "Invalid Account Number provided by client."),

    InvalidEncryption(SC_BAD_REQUEST,
            "We are unable to decrypt the signature using the RSA public key for this client"),

    InvalidPAN(SC_OK, "Invalid PAN Number provided by client."),

    InvalidToken(SC_OK, "Invalid token provided by client."),

    InvalidReturnURL(SC_OK, "Invalid returnURL provided by client."),

    InvalidYears(SC_OK, "Invalid Number of Years provided by client."),

    MalformedXML(SC_BAD_REQUEST, "Client has sent malformed XML for the API"),

    MethodNotAllowed(SC_METHOD_NOT_ALLOWED, "The specified method is not allowed for the API"),

    NotSignedUp(SC_FORBIDDEN, "Client has not signed up for the Perfios Insights-Assist Service"),

    SignatureDoesNotMatch(SC_BAD_REQUEST,
            "The request signature we calculated does not match the signature you provided."),

    SiteError(SC_OK, "Site is in-accessible."),

    TokenNotFound(SC_NOT_FOUND, "We could not find the Perfios Token referred to by the Client"),

    /* To be filled by consumer */
    UserMessage(SC_OK, "UserMessage");

    private final String defaultErrorMessage;

    private final int statusCode;

    private API_ERROR_CODE() {
        this(SC_INTERNAL_SERVER_ERROR, null);
    }

    private API_ERROR_CODE(final int statusCode, final String defaultErrorMessage) {
        this.statusCode = statusCode;
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
