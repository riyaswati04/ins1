package com.ia.services;

import javax.servlet.http.HttpServletRequest;

import com.ia.actions.Action;
import com.ia.core.services.Service;

public interface ActionService extends Service {

    Action getAction(HttpServletRequest request);

}
