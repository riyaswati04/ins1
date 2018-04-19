package com.ia.admin;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.inject.Inject;
import com.ia.generated.tables.IaUser;

import com.google.inject.Singleton;

import com.ia.core.annotations.ActionPath;
import com.ia.actions.AbstractAction;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.jooq.tools.StringUtils.*;

import com.ia.beans.GenericResultBean;
import org.jooq.DSLContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Singleton
@ActionPath("/api/v1/addUser")


public class StoreUserData extends AbstractAction
{

    private final DSLContext dslContext;
    Pattern pattern;
    Matcher matcher;


    @Inject
    public StoreUserData(final DSLContext dslContext){
        super();
        this.dslContext=dslContext;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
	final GenericResultBean result = new GenericResultBean(false);
         String emailPattern ="^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";

         response.setContentType("application/json");
        String orgId = request.getParameter("organisation");
        String email = request.getParameter("email");
        pattern = Pattern.compile(emailPattern);

        String password = request.getParameter("password");

        if(isEmpty(orgId)||isEmpty(password)||isEmpty(email)){
            result.setSuccess(false);
            result.setMessage("Either email or password or organisation  is null");
            sendResponse(response,result);
            return;
        }
        password = sha1(password);
        int organisationId = Integer.parseInt(orgId);

        if(validateEmail(email)){
            logger.info("Email valid");
            if (addUser(organisationId, email, password)) {
            result.setSuccess(true);

            sendResponse(response, result);

        }
        } else {
                    result.setMessage("error");
                    response.setStatus(SC_BAD_REQUEST);
                    sendResponse(response, result);
                    return;


                }


    }

    public boolean addUser(int organisationId,String email, String pass) {
       boolean status= false;

        try {
            dslContext.insertInto(IaUser.IA_USER,

                    IaUser.IA_USER.ORGANISATION_ID,

                    IaUser.IA_USER.EMAIL_ID,

                    IaUser.IA_USER.PASSWORD)

                    .values(

                            organisationId,

                            email,

                            pass)

                    .execute();
            status = true;
        }catch(Exception e){
            logger.error(e,"Exception caught");
        }

        return status;
    }
    private String sha1(final String input) throws NoSuchAlgorithmException {
        final MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        final byte[] result = mDigest.digest(input.getBytes());
        final StringBuffer sb = new StringBuffer();

        for (final byte element : result) {
            sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }


    public boolean validateEmail(String email) {

        matcher = pattern.matcher(email);
        return matcher.matches();

    }

   @Override
    public boolean requiresLogin() {
        return true;
    }
}

