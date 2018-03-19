package com.ia.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;
import com.ia.util.DatabaseUtil;

@Singleton
@ActionPath("/api/v1/downloadReport")
public class DownloadReport extends AbstractAction {

    private final DatabaseUtil databaseUtil;

    @Inject
    public DownloadReport(final DatabaseUtil databaseUtil) {
        this.databaseUtil = databaseUtil;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final String clientTransactionId = request.getParameter("clientTransactionId");
        final String reportLocation = databaseUtil.fetchReportLocation(clientTransactionId);
        try {
            if (reportLocation != null) {
                final File pdfFile = new File(reportLocation);

                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + clientTransactionId + ".pdf");
                response.setContentLength((int) pdfFile.length());

                final FileInputStream fileInputStream = new FileInputStream(pdfFile);
                final OutputStream responseOutputStream = response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
                fileInputStream.close();
            }

        }
        catch (final Exception e) {
            logger.error("Exception while uploading the report" + e.getMessage());
            response.sendError(500);
        }

    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

}
