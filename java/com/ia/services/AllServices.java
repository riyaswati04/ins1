package com.ia.services;

import static com.google.common.collect.ImmutableList.builder;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.ia.core.services.Service;

public final class AllServices {

    public static SessionService sessionService;

    public static ActionService actionService;

    public static TemplateService templateService;

    public static OrganisationService organisationService;

    public static MailerService mailerService;

    public static List<Class<? extends Service>> getServiceClasses() {
        final ImmutableList.Builder<Class<? extends Service>> listBuilder = builder();
        listBuilder.add(MailerService.class);
        listBuilder.add(ActionService.class);
        listBuilder.add(SessionService.class);
        listBuilder.add(TemplateService.class);
        listBuilder.add(OrganisationService.class);
        return listBuilder.build();
    }
}
