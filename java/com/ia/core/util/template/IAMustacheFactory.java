package com.ia.core.util.template;

import static com.google.common.base.Charsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;

public final class IAMustacheFactory extends DefaultMustacheFactory {
    private final boolean debugMode;

    private final File fileRoot;

    public IAMustacheFactory(final File fileRoot, final boolean debugMode) {
        super(fileRoot);

        this.fileRoot = fileRoot;
        this.debugMode = debugMode;
    }

    @Override
    public Mustache compile(final String name) {
        return

        /* Are we running in debug mode? (e.g. in development) */
        debugMode ?

        /* We want to re-read templates in debug mode. */
                super.compile(getReader(name), name) :

                /* DefaultMustacheFactory caches templates. So use the cache in non-debug mode. */
                super.compile(name);
    }

    @Override
    public Reader getReader(final String resourceName) {

        /* Initialise InputStream. */
        InputStream is = null;

        /* Create a File instance pointing to the resource. */
        final File file = new File(fileRoot, resourceName);

        /* Test if the file exists. */
        if (file.isFile()) {

            /* File exists. Open stream. */
            is = openStream(file);

        }

        if (is == null) {
            throw new MustacheException(
                    "Template '" + resourceName + "' not found at " + file.getAbsolutePath());
        }

        return new BufferedReader(new InputStreamReader(is, UTF_8));
    }

    private FileInputStream openStream(final File file) {

        try {
            return new FileInputStream(file);

        }
        catch (final FileNotFoundException e) {
            throw new MustacheException("Found file, could not open: " + file, e);

        }

    }
}
