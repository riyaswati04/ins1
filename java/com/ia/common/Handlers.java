package com.ia.common;

import com.google.inject.Injector;
import com.ia.actions.service.CheckExpiryDate;
import com.ia.crypto.CryptHandler;
import com.ia.log.KLogger;
import com.ia.scheduler.KJobScheduler;

/**
 * Static data of all Kubera handlers and managers. Use static import of this class wherever these
 * handlers are required. Ensure that there are no name clashes.
 */
public final class Handlers {

    public static Injector injector;

    public static KLogger logger;

    public static CryptHandler cryptHandler;

    public static KJobScheduler kJobScheduler;
    
    public static CheckExpiryDate checkExpiryDate;

}
