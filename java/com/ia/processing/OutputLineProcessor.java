package com.ia.processing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.io.LineProcessor;
import com.ia.processing.impl.ScriptOutputLineImpl;

public final class OutputLineProcessor implements LineProcessor<Queue<ScriptOutputLine>> {

    private final AtomicInteger lineCount = new AtomicInteger(0);

    private final Queue<ScriptOutputLine> lines = new LinkedList<ScriptOutputLine>();

    @Override
    public Queue<ScriptOutputLine> getResult() {
        return lines;
    }

    @Override
    public boolean processLine(final String line) throws IOException {

        final int currentLineNumber = lineCount.incrementAndGet();

        lines.add(new ScriptOutputLineImpl(currentLineNumber, line));

        return true;
    }
}
