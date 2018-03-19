package com.ia.processing.impl;

import com.ia.processing.ScriptOutputLine;

public final class ScriptOutputLineImpl implements ScriptOutputLine {
    private final String contents;

    private final int lineNumber;

    public ScriptOutputLineImpl(final int lineNumber, final String contents) {
        super();
        this.lineNumber = lineNumber;
        this.contents = contents;
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "[lineNumber#" + lineNumber + " contents:" + contents + "]";
    }

}
