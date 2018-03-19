package com.ia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class HttpUtil {
    private static HttpURLConnection getConnection(final URL url, final boolean useProxy)
            throws IOException {
        HttpURLConnection con = null;

        con = (HttpURLConnection) url.openConnection();
        return con;
    }

    public static String post(final String url, final HashMap<String, String> params) {
        try {
            final StringBuilder parameters = new StringBuilder();
            for (final String key : params.keySet()) {
                if (!StringUtils.isEmpty(parameters.toString())) {
                    parameters.append("&");
                }

                parameters.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));
            }

            System.out.println(
                    "Sending POST request to " + url + ", Params: " + parameters.toString());

            final byte[] postData = parameters.toString().getBytes();
            final int postDataLength = postData.length;
            final HttpURLConnection conn = getConnection(new URL(url), true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            final OutputStream os = conn.getOutputStream();
            os.write(postData);
            final StringBuilder output = new StringBuilder();
            System.out.println(conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                final BufferedReader in =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s = in.readLine();
                while (s != null) {
                    output.append(s);
                    s = in.readLine();
                }
            }
            else {
                System.out.println(conn.getResponseMessage());
                if (conn.getErrorStream() != null) {
                    final BufferedReader in =
                            new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String s = in.readLine();
                    while (s != null) {
                        output.append(s);
                        s = in.readLine();
                    }
                }
                else {
                    output.append(conn.getResponseMessage());
                }
            }
            conn.disconnect();
            System.out.println("Output : " + output.toString());
            return output.toString();
        }
        catch (final IOException e) {
            System.out.println("Error while getting the response for " + url + e);
        }

        return null;
    }
}
