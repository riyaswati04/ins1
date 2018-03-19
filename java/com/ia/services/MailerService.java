package com.ia.services;

import static com.ia.common.IAProperties.emailRefreshToken;
import static com.ia.common.IAProperties.machineName;
import static com.ia.common.IAProperties.supportEmailId;
import static com.ia.log.LogUtil.getLogger;
import static java.lang.ClassLoader.getSystemClassLoader;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.ia.core.services.Service;
import com.ia.log.Logger;
import com.perfios.mail.SendGMail;

public class MailerService implements Service {

    private static final Logger logger = getLogger(MailerService.class);

    private static SendGMail mailer = null;

    public static void sendMail(final Set<String> toAddress, final String body,
            final String subject) {
        if (mailer != null) {
            mailer.sendEmail(toAddress, new HashSet<String>(), new HashSet<String>(), subject,
                    body);
        }
    }

    private String subjectPrefix = "";

    private HashSet<String> supportAlertEMailIds = null;

    @Inject
    public MailerService() {}

    @Override
    public void restart() throws Exception {
        logger.info("Restarting mailer service");
        start();
    }

    public void sendAttachments(final Set<String> toAddress, final String body,
            final String subject, final Set<String> files) {
        if (mailer != null) {
            mailer.sendEmailWithAttachment(toAddress, new HashSet<String>(), new HashSet<String>(),
                    subject, body, files);
        }
    }

    public void sendMailToSupport(final String body, final String subject) {
        sendMail(supportAlertEMailIds, body, subjectPrefix + subject);
    }

    @Override
    public void start() throws Exception {

        if (isEmpty(emailRefreshToken)) {
            logger.error("No email refresh token found, mailer service will not be initiated");
            return;
        }

        supportAlertEMailIds = new HashSet<String>();
        supportAlertEMailIds.add(supportEmailId);

        ClassLoader cl = this.getClass().getClassLoader();

        if (cl == null) {
            cl = getSystemClassLoader();
        }

        final URL url = cl.getResource("/client.json");

        final String clientJSONFile = url.getPath();

        logger.debug("clientJSONFIle", clientJSONFile);

        try {
            mailer = new SendGMail(emailRefreshToken, clientJSONFile);

            logger.info("Initiated mail service");

            subjectPrefix = "[" + machineName + "] ";
        }
        catch (final IOException e) {
            logger.exception("Error in initialization mailer", e);
        }
    }

    @Override
    public void stop() throws Exception {

    }

}
