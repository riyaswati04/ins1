package com.ia.admin;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.actions.AbstractAction;
import com.ia.beans.GenericResultBean;
import com.ia.beans.User;
import com.ia.core.annotations.ActionPath;
import com.ia.generated.tables.IaUser;
import com.ia.log.Logger;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.ia.log.LogUtil.getLogger;

@Singleton

@ActionPath("/api/v1/userDropDown")

public class UserDropdown extends AbstractAction {
    public List<User> userList = new ArrayList<User>();

    private final Logger logger = getLogger(getClass());
    private final DSLContext dslContext;



    @Inject
    public UserDropdown(final DSLContext dslContext){
        super();
        this.dslContext=dslContext;
    }



    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        final GenericResultBean result = new GenericResultBean(false);

        response.setContentType("application/json");

        retrieveUserDetails();
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(new Gson().toJson(userList));

    }

    public void retrieveUserDetails() {
        try{
            userList = dslContext

                    .select(IaUser.IA_USER.EMAIL_ID)

                     .from(IaUser.IA_USER)

                    .fetch()

                    .into(User.class);

        }  catch (Exception e) {
            logger.error(e,"Exception caught");
        }



    }



    @Override
    public boolean requiresLogin() {
        return true;
    }
}