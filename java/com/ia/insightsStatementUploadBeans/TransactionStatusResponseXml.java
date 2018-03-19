package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Status")
public class TransactionStatusResponseXml {

    @XStreamAlias("files")
    @XStreamAsAttribute
    private String files;

    @XStreamAlias("txnId")
    @XStreamAsAttribute
    private String transactionId;

    @XStreamAlias("parts")
    @XStreamAsAttribute
    private int parts;

    @XStreamAlias("processing")
    @XStreamAsAttribute
    private String processing;

    @XStreamAlias("Part")
    private StatusPart part;

    public String getFiles() {
        return files;
    }

    public StatusPart getPart() {
        return part;
    }

    public int getParts() {
        return parts;
    }

    public String getProcessing() {
        return processing;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setFiles(final String files) {
        this.files = files;
    }

    public void setPart(final StatusPart part) {
        this.part = part;
    }

    public void setParts(final int parts) {
        this.parts = parts;
    }

    public void setProcessing(final String processing) {
        this.processing = processing;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

}
