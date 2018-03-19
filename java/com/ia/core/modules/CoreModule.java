package com.ia.core.modules;

import com.google.inject.AbstractModule;
import com.ia.actions.modules.ActionsModule;
import com.ia.core.CoreServicesModule;
import com.ia.core.ServiceModule;
import com.ia.core.UtilsModule;
import com.ia.core.forms.CoreFormsModule;
import com.ia.forms.FormsModule;

public final class CoreModule extends AbstractModule {

    @Override
    protected void configure() {

        /* Core Services */
        install(new CoreServicesModule());

        /* Utils Module */
        install(new UtilsModule());

        /* Service Module */
        install(new ServiceModule());

        /* Actions Module */
        install(new ActionsModule());

        /* Core forms */
        install(new CoreFormsModule());

        /* Forms Module */
        install(new FormsModule());

    }
}
