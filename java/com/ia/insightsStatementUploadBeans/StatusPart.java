package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Part")
public class StatusPart {
    @XStreamAlias("errorCode")
    @XStreamAsAttribute
    private String errorCode;

    @XStreamAlias("perfiosTransactionId")
    @XStreamAsAttribute
    private String perfTxnId;

    @XStreamAlias("status")
    @XStreamAsAttribute
    private String status;

    @XStreamAlias("reason")
    @XStreamAsAttribute
    private String reason;

    public String getErrorCode() {
        return errorCode;
    }

    public String getPerfTxnId() {
        return perfTxnId;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public void setPerfTxnId(final String perfTxnId) {
        this.perfTxnId = perfTxnId;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

}
