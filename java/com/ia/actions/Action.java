package com.ia.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {

    void execute(HttpServletRequest request, HttpServletResponse response) throws Exception;

    boolean requiresLogin();
}
