package com.ia.admin;
import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.inject.Inject;
import com.ia.beans.TransactionBean;
import com.ia.generated.tables.IaTransactions;
import com.ia.log.Logger;
import com.google.inject.Singleton;
import com.ia.core.annotations.ActionPath;
import com.ia.actions.AbstractAction;


import static com.ia.util.TempFileUtil.createTempDir;
import static com.ia.log.LogUtil.getLogger;

import java.util.ArrayList;

import com.ia.beans.GenericResultBean;
import org.jooq.DSLContext;
import org.json.JSONObject;


import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


@Singleton
@ActionPath("/api/v1/transactions")

public class TransactionDetails extends AbstractAction {
    private final DSLContext dslContext;

    final GenericResultBean result = new GenericResultBean(false);
    public TransactionBean details = null;
    public List<TransactionBean> userTransactions = new ArrayList<TransactionBean>();
    public File Fname;
    private final Logger logger = getLogger(getClass());


    @Inject
    public TransactionDetails( final DSLContext dslContext) {
        super();
        this.dslContext = dslContext;

    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        retrieveTransactionDetails(response);
        String filePath = Fname + "/" + "transactions.csv";
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);
        response.setContentType("application/octet-stream");
        response.setContentLength((int) downloadFile.length());
        response.setStatus(200);
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close();
    }

    public void retrieveTransactionDetails(final HttpServletResponse response) {
        try {

            userTransactions = dslContext

                    .selectFrom(IaTransactions.IA_TRANSACTIONS)
                    .fetch()
                    .into(TransactionBean.class);

             int maxIndex=userTransactions.size();
             for(int index=0;index<maxIndex;index++) {
                details=new TransactionBean();
                details.setTransactionId(userTransactions.get(index).transactionId);
                details.setPerfiosTransactionId(userTransactions.get(index).perfiosTransactionId);
                details.setReportLocation(userTransactions.get(index).reportLocation);
                details.setFormDataJson(userTransactions.get(index).formDataJson);
                JSONObject jsonObj = new JSONObject(details.formDataJson);
                details.setEmailId(jsonObj.getString("emailId"));
                details.setLoanAmount(jsonObj.getString("loanAmount"));
                details.setLoanDuration(jsonObj.getString("loanDuration"));
                details.setLoanType(jsonObj.getString("loanType"));
                details.setOrganisation(jsonObj.getString("organisation"));
                details.setType(jsonObj.getString("type"));
                details.setMobileNumber(jsonObj.getString("mobileNumber"));
                details.setName(jsonObj.getString("name"));
                userTransactions.set(index,details);
             }

        } catch (final Exception e) {
            logger.error(e, "Exception caught");
        }
        if(details==null){
            result.setSuccess(false);
            result.setMessage("No data present to be written to file");
            sendResponse(response,result);
            return;
        }
        else {
            Fname = createTempDir();
            String fileName = Fname + "/" + "transactions.csv";
            writeCSVFile(fileName);
        }

    }



     public  void writeCSVFile(String fileName) {
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        String FILE_HEADER = "transactionId,perfiosTransactionId,name,emailId,mobileNumber,type,organisation,loanType,loanAmount,loanDuration,reportLocation";
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE_SEPARATOR);
            for(TransactionBean details : userTransactions) {
                fileWriter.append(String.valueOf(details.getTransactionId()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getPerfiosTranscationId()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getName()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getEmailId()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getMobileNumber()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getType()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getOrganisation()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getLoanType()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getLoanAmount()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getLoanDuration()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(details.getReportLocation()));
                fileWriter.append(NEW_LINE_SEPARATOR);
                }
        }
        catch(Exception e){
            logger.error(e,"Exception caught");

        }
        finally{
            try{
                fileWriter.flush();
                fileWriter.close();
            }
            catch (IOException e) {
                logger.error(e,"Exception caught");

            }
        }
    }


    @Override
    public boolean requiresLogin() {
        return true;
    }
}
