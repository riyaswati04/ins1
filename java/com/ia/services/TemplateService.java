package com.ia.services;

import java.io.File;

import com.github.mustachejava.Mustache;
import com.ia.core.services.Service;

public interface TemplateService extends Service {
    Mustache compile(String templateContents);

    Mustache getTemplate(String templateName);

    File getTemplateDir();
}
