package com.ia.insightsStatementUploadBeans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Success")
public class GenerateUrlSuccess {

    private String url;

    private String expires;

    public Date convertDate() throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        final Date expiresDate = sdf.parse(expires);
        return expiresDate;
    }

    public String getExpires() {
        return expires;
    }

    public String getUrl() {
        return url;
    }

    public void setExpires(final String expires) {
        this.expires = expires;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

}
