package com.ia.admin;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.AbstractAction;
import com.ia.beans.GenericResultBean;
import com.ia.beans.ITOnlineStartRequestBean;
import com.ia.beans.Organisation;
import com.ia.beans.impl.OrganisationImpl;
import com.ia.core.annotations.ActionPath;
import com.ia.log.Logger;
import com.ia.services.impl.OrganisationServiceImpl;
import org.bouncycastle.util.encoders.Hex;
import org.jooq.tools.StringUtils;


import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ia.log.LogUtil.getLogger;
import static com.ia.services.MailerService.sendMail;

@Singleton
@ActionPath("/api/v1/startRequest")

public class ITOnlineStartRequest extends AbstractAction
{

    private final Logger logger = getLogger(getClass());
    private OrganisationServiceImpl organisationService;
    private OrganisationImpl organisation ;
    boolean status = false;
    Key privateKey ;
    RSAPrivateCrtKey pvt;
    String jstring;
    Gson gson = new Gson();
    Pattern pattern;
    Matcher matcher;


    @Inject
    ITOnlineStartRequest(OrganisationServiceImpl organisationService,OrganisationImpl organisation){
        super();
        this.organisationService = organisationService;
        this.organisation = organisation;
    }


    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);
        String emailPattern ="^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$";
        response.setContentType("application/json");
        String emailID=request.getParameter("email");
        String panPattern = "^[A-Z]{5}[0-9]{4}[A-Z]$";


        pattern = Pattern.compile(emailPattern);
        if(!validate(emailID)){
            result.setSuccess(false);
            return;
            }

            pattern=Pattern.compile(panPattern);



        String json=request.getParameter("json");



        Organisation org = organisationService.getOrganisation("acme");
        Optional<PrivateKey> pvtKey = organisationService.getSignatureKey(org);

        try {
            privateKey =pvtKey.get();
            pvt = (RSAPrivateCrtKey)privateKey ;

        } catch (Exception e) {
            logger.error(e,"exception caught");
        }
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(pvt.getModulus(), pvt.getPublicExponent());
        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            ITOnlineStartRequestBean bean=gson.fromJson(json,ITOnlineStartRequestBean.class);
            if (StringUtils.isEmpty(bean.getPan())||StringUtils.isEmpty(bean.getRedirectUrl())||StringUtils.isEmpty(bean.getDob())||StringUtils.isEmpty(bean.getClientTxnId())||StringUtils.isEmpty(bean.getFetchForm26AS())||StringUtils.isEmpty(bean.getFetchITR())||StringUtils.isEmpty(bean.getNoOfYearsForForm26AS())||StringUtils.isEmpty(bean.getNoOfYearsForITR())||StringUtils.isEmpty(bean.getType())){
                result.setSuccess(false);
                result.setMessage("Field is null");
                sendResponse(response, result);
                return;
            }

            if(!validate(bean.getPan())){
                result.setSuccess(false);
                return;
            }


            final byte[] encryptedPan = cipher.doFinal(bean.getPan().getBytes("UTF-8"));
            final byte[] encoded = Hex.encode(encryptedPan);
            String strEncrypted = new String(encoded);
            bean.setPan(strEncrypted);
            jstring=gson.toJson(bean);
            logger.info("jstring:"+jstring);

            Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decrypt.init(Cipher.DECRYPT_MODE,privateKey);
            byte[] decByte = decrypt.doFinal(encryptedPan);
            String decoded = new String(decByte);
            result.setSuccess(true);
            String encodedQuery = URLEncoder.encode(jstring, "UTF-8");
            String postData = "json=" + encodedQuery;



            URL url = new URL("https://ekycdemo.perfios.com/KYCServer/kyc/api/v1/it/online/start/acme");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",  String.valueOf(postData.length()));

            // Write data
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());

            // Read response
            StringBuilder responseSB = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ( (line = br.readLine()) != null)
                responseSB.append(line);

            // Close streams
            br.close();
            os.close();


            String responseString = responseSB.toString();

            JsonObject jsonObject = new JsonParser().parse(responseString).getAsJsonObject();
            String responseUrl = jsonObject.get("url").getAsString();
            final Set<String> emailIdSet = new HashSet<String>();
            emailIdSet.add(emailID);

            sendMail(emailIdSet, "Please click the insights link to proceed:" + responseUrl,
                    "Perfios Insights.");



            response.getWriter().write(new Gson().toJson(responseString));


        } catch (Exception e) {
            logger.error(e,"exception caught");
            sendResponse(response,result);

        }



    }
    public boolean validate(String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }




    @Override
    public boolean requiresLogin() {
        return true;
    }
}