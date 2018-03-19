package com.ia.core.forms;

import javax.servlet.http.HttpServletRequest;

public interface FormFactory {

    Form create(HttpServletRequest request) throws Exception;

}
