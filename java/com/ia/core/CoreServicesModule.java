package com.ia.core;

import static com.google.inject.jndi.JndiIntegration.fromJndi;
import static java.util.ResourceBundle.getBundle;
import static org.jooq.SQLDialect.MYSQL;

import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.quartz.spi.JobFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.ia.actions.paths.Paths;
import com.ia.actions.paths.impl.PathsImpl;
import com.ia.core.database.ConnectionProviderImpl;
import com.ia.core.database.DSLContextProvider;
import com.ia.crypto.CryptHandler;
import com.ia.log.KLogger;
import com.ia.scheduler.GuiceAwareJobFactory;
import com.ia.scheduler.KJobScheduler;
import com.ia.services.ActionService;
import com.ia.services.MailerService;
import com.ia.services.OrganisationService;
import com.ia.services.SessionService;
import com.ia.services.TemplateService;
import com.ia.services.impl.ActionServiceImpl;
import com.ia.services.impl.OrganisationServiceImpl;
import com.ia.services.impl.SessionServiceImpl;
import com.ia.services.impl.TemplateServiceImpl;

public class CoreServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Context.class).to(InitialContext.class);

        bind(OrganisationServiceImpl.class).in(Singleton.class);

        bind(OrganisationService.class).to(OrganisationServiceImpl.class);

        bind(DataSource.class).toProvider(fromJndi(DataSource.class, "java:comp/env/jdbc/MySQLDB"))
                .in(Singleton.class);

        bind(ConnectionProvider.class).to(ConnectionProviderImpl.class).in(Singleton.class);

        bind(SQLDialect.class).toInstance(MYSQL);

        bind(DSLContext.class).toProvider(DSLContextProvider.class).in(Singleton.class);

        bind(ResourceBundle.class).toInstance(getBundle("ia"));

        bind(KLogger.class).in(Singleton.class);

        bind(CryptHandler.class).in(Singleton.class);

        bind(ActionService.class).to(ActionServiceImpl.class).in(Singleton.class);

        bind(SessionService.class).to(SessionServiceImpl.class).in(Singleton.class);

        bind(TemplateService.class).to(TemplateServiceImpl.class).in(Singleton.class);

        //bind(KJobScheduler.class).in(Singleton.class);

        bind(JobFactory.class).to(GuiceAwareJobFactory.class).in(Singleton.class);

        bind(Paths.class).to(PathsImpl.class).in(Singleton.class);

        bind(MailerService.class).in(Singleton.class);
    }
}
