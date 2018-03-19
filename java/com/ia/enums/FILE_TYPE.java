package com.ia.enums;

public enum FILE_TYPE {

    DAT("text", "application/text"),

    PDF("pdf", "application/pdf"),

    TEXT("text", "application/text"),

    XLS("xls", "application/vnd.ms-excel"),

    XML("xml", "application/xml"),

    JSON("xml", "application/json"),

    ZIP("zip", "application/zip");

    private final String contentType;

    private final String fileNameExtension;

    private FILE_TYPE(final String fileNameExtension, final String contentType) {
        this.fileNameExtension = fileNameExtension;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileNameExtension() {
        return fileNameExtension;
    }

}
