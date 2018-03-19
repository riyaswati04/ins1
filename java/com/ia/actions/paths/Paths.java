package com.ia.actions.paths;

import com.ia.enums.HTTP_METHOD;
import com.ia.enums.MODE;

public interface Paths {

    HTTP_METHOD getMethodExpected(String path);

    boolean requiresSignature(String path);

    MODE getPermissionExpected(String path);

    boolean requiresSignature(String path, String organisation);

    boolean verifyMethod(String path, HTTP_METHOD method);

    boolean verifyReferer(String path);
}
