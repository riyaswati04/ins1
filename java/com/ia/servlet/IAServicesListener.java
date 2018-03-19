package com.ia.servlet;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.collect.Lists.reverse;
import static com.ia.common.Handlers.cryptHandler;
import static com.ia.common.Handlers.injector;
import static com.ia.common.Handlers.kJobScheduler;
import static com.ia.common.Handlers.logger;
import static com.ia.common.Handlers.checkExpiryDate;
import static com.ia.common.IAProperties.getProperty;
import static com.ia.common.IAProperties.load;
import static com.ia.common.IAProperties.productionSystem;
import static com.ia.services.AllServices.getServiceClasses;
import static java.lang.String.format;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import com.ia.actions.service.CheckExpiryDate;
import com.ia.common.IARuntimeException;
import com.ia.core.AbstractInjectableServletContextListener;
import com.ia.core.services.Service;
import com.ia.crypto.CryptHandler;
import com.ia.log.KLogger;
import com.ia.scheduler.KJobScheduler;
import com.ia.services.AllServices;
import com.ia.util.TempFileUtil;

public final class IAServicesListener extends AbstractInjectableServletContextListener {

    private static final String LOG_EXCEPTION_SHUTTING_DOWN = "Exception shutting down services.";

    private static final String LOG_MISSING_REFERENCE_FIELD =
            "Please add a field named %s to %s to save a reference to the %s instance.";

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        final List<Class<? extends Service>> klasses = reverse(getServiceClasses());

        logger.info(productionSystem ? "--- Shutdown in PRODUCTION MODE ---"
                : "--- Shutdown in DEVELOPMENT MODE ---");

        for (final Class<? extends Service> klass : klasses) {
            try {
                final Service instance = getInstance(sce, klass);
                instance.stop();

                /* Remove reference to this service from AllServices. */
                removeReferenceToService(klass);

            }
            catch (final Exception e) {
                logger.error(LOG_EXCEPTION_SHUTTING_DOWN, e);
            }
        }

        try {
            checkExpiryDate.shutdown();
        }
        catch (final Exception e) {
            System.err.println("Exception during shutdown of check expiry date handler: " + e);
        }
        try {
            logger.shutdown();
        }
        catch (final Exception e) {
            System.err.println("Exception during shutdown of Logger: " + e);
        }
        
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        injector = getInjector(sce);

        final ResourceBundle resourceBundle = getInstance(sce, ResourceBundle.class);

        load(resourceBundle);

        logger = getInstance(sce, KLogger.class);
        logger.start();

        cryptHandler = getInstance(sce, CryptHandler.class);
        cryptHandler.start();

        //kJobScheduler = getInstance(sce, KJobScheduler.class);
        //kJobScheduler.start();
        
        checkExpiryDate = getInstance(sce, CheckExpiryDate.class);
        checkExpiryDate.start();
        
        
        
        

        try {

            logger.info(productionSystem ? "--- Running in PRODUCTION MODE ---"
                    : "--- Running in DEVELOPMENT MODE ---");

            TempFileUtil.init(getProperty("tempFiles.rootDir"));

            // Signal scheduler to fire crons if required (in case missed due to server shutdown
            // etc)
            // Note: If running a cron job requires other resources,
            // ensure that all resources are acquired before starting crons
            // NOTE: Start cron jobs after all cache are loaded and all services are initiated.
            // kJobScheduler.startCronJobs();

            for (final Class<? extends Service> klass : getServiceClasses()) {
                final Service instance = getInstance(sce, klass);
                instance.start();
                /* Save a reference to this service in AllServices. */
                saveReferenceToService(klass, instance);
            }

        }
        catch (final Exception e) {
            logger.error("IA Server Servlet initialization failed!", e);
            throw new IARuntimeException("IA Server Servlet Initialization failed");
        }

        System.err.println("------- Servlet initialization completed ------");
        logger.info("------- Servlet initialization completed ------");
    }

    private Field getReferenceField(final Class<? extends Service> klass)
            throws NoSuchFieldException, ServletException {
        final Class<AllServices> mklass = AllServices.class;
        final String referenceFieldName = getReferenceFieldName(klass);
        final Field reference = mklass.getDeclaredField(referenceFieldName);

        /* No field corresponding to klass. */
        if (null == reference) {
            final String message =
                    format(LOG_MISSING_REFERENCE_FIELD, referenceFieldName, mklass, klass);

            throw new ServletException(message);
        }

        return reference;
    }

    private String getReferenceFieldName(final Class<? extends Service> klass) {
        /* Reference field name is lower camel case of the class's simple name. */
        final String className = klass.getSimpleName();
        return UPPER_CAMEL.to(LOWER_CAMEL, className);
    }

    private void removeReferenceToService(final Class<? extends Service> klass)
            throws NoSuchFieldException, ServletException, IllegalArgumentException,
            IllegalAccessException {
        /* Remove the reference to the instance. */
        final Field reference = getReferenceField(klass);
        reference.set(null, null);
    }

    private void saveReferenceToService(final Class<? extends Service> klass,
            final Service instance) throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, ServletException {
        final Field reference = getReferenceField(klass);
        reference.set(null, instance);
    }
}
