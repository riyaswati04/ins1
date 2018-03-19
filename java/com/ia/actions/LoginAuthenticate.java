package com.ia.actions;

import static com.ia.core.util.IAConstants.CONTENT_TYPE_APPLICATION_JSON;
import static com.ia.log.LogUtil.getLogger;
import static com.ia.util.JsonUtil.toJson;
import static com.ia.util.http.HttpUtil.getSession;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.beans.GenericResultBean;
import com.ia.beans.User;
import com.ia.core.annotations.ActionPath;
import com.ia.core.forms.FormBuilder;
import com.ia.fetchdata.UserCredentialAuthenticate;
import com.ia.forms.LoginForm;
import com.ia.log.Logger;
import com.ia.services.SessionService;

@Singleton
@ActionPath("/api/v1/login")
public class LoginAuthenticate extends AbstractAction {

    private static final String INVALID_JSON = "INVALID_JSON";

    private static final String LOG_ERROR_MALFORMED_JSON = "Malformed json sent by host ";

    private static final String INVALID_PARAMETER = "INVALID_PARAMETER";

    private static final String LOG_ERROR_INVALID_PARAMETER = "Invalid parameter sent by host";

    private static final String LOG_ERROR_INVALID_FORM = "Invalid Form";

    private final SessionService service;

    private final FormBuilder formBuilder;

    private final UserCredentialAuthenticate userCredentialAuthenticate;

    private final Logger logger = getLogger(getClass());

    @Inject
    public LoginAuthenticate(final SessionService service,
            final UserCredentialAuthenticate userCredentialAuth, final FormBuilder formBuilder) {
        super();
        this.formBuilder = formBuilder;
        this.service = service;
        userCredentialAuthenticate = userCredentialAuth;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        response.setContentType(CONTENT_TYPE_APPLICATION_JSON);

        final GenericResultBean result = new GenericResultBean(false);

        final Class<LoginForm> formClass = LoginForm.class;

        LoginForm form = null;

        final String ipAddress = request.getRemoteAddr();

        try {
            form = formBuilder.fromJson(formClass, null, request);
        }
        catch (final Exception e) {
            logger.error(e, LOG_ERROR_MALFORMED_JSON);
            result.setMessage(INVALID_JSON);
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        /* Empty */
        if (form == null) {
            logger.error(LOG_ERROR_INVALID_PARAMETER);
            result.setMessage(INVALID_PARAMETER);
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        /* Invalid form */
        if (!form.validate()) {
            logger.error(LOG_ERROR_INVALID_FORM);
            result.setMessage(form.getFirstErrorMessage());
            response.setStatus(SC_BAD_REQUEST);
            sendResponse(response, result);
            return;
        }

        final String username = form.getUserName();

        final String password = form.getPassword();

        final HttpSession session = getSession(request);

        logger.info("Validate user for emailId=%s, ipAddress=%s", username, ipAddress);

        final User user = userCredentialAuthenticate.authenticate(username, password);

        if (user != null) {
            service.handleUserLogIn(session.getId(), user);

            if (service.isLoggedIn(request.getSession().getId())) {
                result.setSuccess(true);
                result.setMessage(toJson(user));
                sendResponse(response, result);
                // TODO:change to ia form page
            }
        }
        else {
            result.setSuccess(false);
            result.setMessage("No User Available");
            sendResponse(response, result);
        }
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }
}
