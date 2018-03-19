package com.ia.services.impl;

import static com.ia.common.IAProperties.servletDebug;
import static com.ia.common.IAProperties.templateBaseDir;
import static com.ia.log.LogUtil.getLogger;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.inject.Inject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.Singleton;
import com.ia.core.util.template.IAMustacheFactory;
import com.ia.log.Logger;
import com.ia.services.TemplateService;

@Singleton
public class TemplateServiceImpl implements TemplateService {

    private static final String LOG_INFO_RESTART_SERVICE = "Re-Started.";

    private static final String LOG_INFO_START_SERVICE = "Started.";

    private static final String LOG_INFO_STOP_SERVICE = "Stopping.";

    private final MustacheFactory factory;

    private final File fileRoot;

    private final Logger logger = getLogger(getClass());

    private final MustacheFactory stringTemplateFactory;

    @Inject
    public TemplateServiceImpl() {
        super();
        /* Point to enact sub-directory under template root directory. */
        fileRoot = new File(templateBaseDir);

        /* A DefaultMustacheFactory for compiling string templates. */
        stringTemplateFactory = new DefaultMustacheFactory();

        factory = new IAMustacheFactory(fileRoot, servletDebug);
    }

    @Override
    public Mustache compile(final String templateContents) {
        final Reader reader = new StringReader(templateContents);
        return stringTemplateFactory.compile(reader, "stringTemplate");
    }

    @Override
    public Mustache getTemplate(final String templateName) {
        return factory.compile(templateName);
    }

    @Override
    public File getTemplateDir() {
        return fileRoot;
    }

    @Override
    public void restart() throws Exception {
        logger.info(LOG_INFO_RESTART_SERVICE);
    }

    @Override
    public void start() throws Exception {
        logger.info(LOG_INFO_START_SERVICE);
    }

    @Override
    public void stop() throws Exception {
        logger.info(LOG_INFO_STOP_SERVICE);
    }
}
