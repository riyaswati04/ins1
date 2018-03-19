package com.ia.util.http;

import static com.ia.core.util.IAConstants.CONTENT_TYPE_APPLICATION_JSON;
import static com.ia.log.LogUtil.getLogger;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ia.log.Logger;

public class HTTPPost {

    private static final String LOG_ERROR_WHILE_SENDING_RESULT_TO_SPECIFIED_URL =
            "Exception while sending result to specified callBackurl, CallBackUrl = [%s] for token = [%s], message [%s]";

    private static final Logger logger = getLogger(HTTPPost.class);

    @SuppressWarnings("deprecation")
    public static boolean makeHTTPPost(final String url, final String data, final String token)
            throws IOException {

        boolean success = false;

        final HttpClient httpClient = new HttpClient();
        httpClient.setTimeout(1000);

        final PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestBody(data);

        postMethod.setRequestHeader("Accept", CONTENT_TYPE_APPLICATION_JSON);
        postMethod.setRequestHeader("Content-type", CONTENT_TYPE_APPLICATION_JSON);

        try {
            httpClient.executeMethod(postMethod);
            success = true;
        }
        catch (final Exception e) {
            logger.error(LOG_ERROR_WHILE_SENDING_RESULT_TO_SPECIFIED_URL, url, token,
                    e.getMessage());
        }

        return success;
    }
}
