package com.ia.core.util.bean;

import static com.google.common.base.Preconditions.checkArgument;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.TempFileUtil.createTempDir;
import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.ia.log.Logger;

public final class ServletFileUploadUtil {

    private static final String LOG_UPLOADING_FILE_ATTACHMENTS =
            "Uploading file attachments to %s. Max file size permitted is %s";

    private static final String MSG_REQUEST_MUST_BE_MULTIPART = "Request must be multipart.";

    private final Logger logger = getLogger(getClass());

    private final long maxFileSize;

    private final ServletFileUpload servletFileUpload;

    private final File tempDir;

    public ServletFileUploadUtil(final long maxFileSize) {
        this(maxFileSize, createTempDir());
    }

    public ServletFileUploadUtil(final long maxFileSize, final File tempDir) {
        super();
        this.maxFileSize = maxFileSize;
        this.tempDir = tempDir;

        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(1024, tempDir));
        servletFileUpload.setFileSizeMax(maxFileSize);
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public File getTempDir() {
        return tempDir;
    }

    public List<FileItem> parse(final HttpServletRequest request) throws FileUploadException {
        checkArgument(isMultipartContent(request), MSG_REQUEST_MUST_BE_MULTIPART);

        logger.info(LOG_UPLOADING_FILE_ATTACHMENTS, getTempDir(), getMaxFileSize());
        return servletFileUpload.parseRequest(request);
    }

}
