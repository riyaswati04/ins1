package com.ia.processing.impl;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.ia.log.LogUtil.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import com.google.common.io.Files;
import com.google.inject.Singleton;
import com.ia.log.Logger;
import com.ia.processing.OutputLineProcessor;
import com.ia.processing.ReadOutFile;
import com.ia.processing.ScriptOutputLine;

@Singleton
public final class ReadOutFileImpl implements ReadOutFile {

    private static final String MSG_CANNOT_READ = "Cannot read file %s";

    private static final String LOG_READ_LINES_FROM_OUTFILE = "Read %s lines from outfile %s.";

    private static final String LOG_EXCEPTION_READING_FILE =
            "Exception reading lines from file into queue";

    private final Logger logger = getLogger(getClass());

    @Override
    public Queue<ScriptOutputLine> readLines(final File file) {
        /* Assert file exists. */
        checkArgument(null != file && file.isFile(), MSG_CANNOT_READ, file);
        Queue<ScriptOutputLine> lines = null;

        try {
            lines = Files.readLines(file, UTF_8, new OutputLineProcessor());
            logger.info(LOG_READ_LINES_FROM_OUTFILE, lines.size(), file);

        }
        catch (final IOException e) {
            logger.exception(LOG_EXCEPTION_READING_FILE, e);
        }

        return lines;
    }
}
