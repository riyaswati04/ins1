package com.ia.fetchdata;

import static com.ia.log.LogUtil.getLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.beans.User;
import com.ia.generated.tables.IaUser;
import com.ia.log.Logger;

@Singleton
public class UserCredentialAuthenticate {

    private static final String LOG_ERROR_IN_AUTHENTICATION =
            "Query exception while executing query with emailId [%s] ";

    private final Logger logger = getLogger(getClass());

    private final DSLContext dslContext;

    @Inject
    public UserCredentialAuthenticate(final DSLContext dslContext) {
        super();
        this.dslContext = dslContext;
    }

    public User authenticate(final String emailId, String password) {

        User user = null;

        try {
            password = sha1(password);

            final Result<Record> result = dslContext

                    .select()

                    .from(IaUser.IA_USER)

                    .where(IaUser.IA_USER.EMAIL_ID.equal(emailId))

                    .and(IaUser.IA_USER.PASSWORD.equal(password))

                    .fetch();

            if (result.size() != 0) {

                user = new User();

                user.setEmailId(result.get(0).getValue(IaUser.IA_USER.EMAIL_ID));

                user.setUserId(result.get(0).getValue(IaUser.IA_USER.USER_ID));
            }
        }
        catch (final Exception e) {
            logger.error(LOG_ERROR_IN_AUTHENTICATION, emailId);
        }

        return user;
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

}
