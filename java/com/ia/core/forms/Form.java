package com.ia.core.forms;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Form {

    String getErrorMessage(String fieldName);

    Map<String, String> getErrorMessages();

    String getFirstErrorMessage();

    List<String> getNonFieldErrorMessages();

    String getRemoteAddress();

    HttpServletRequest getRequest();

    String getUserAgent();

    void setErrorMessage(String field, String errorMessage);

    void setNonFieldErrorMessage(String errorMessage);

    boolean validate();

    boolean validate(Class<?> validationGroup);

}
