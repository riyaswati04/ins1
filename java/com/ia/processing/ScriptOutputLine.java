package com.ia.processing;

/**
 * Represents one line of output read from a Script <code>out</code> file.
 *
 */
public interface ScriptOutputLine {

    /**
     * The text contents of the line.
     *
     * @return Text contents of the line.
     */
    String getContents();

    /**
     * Number of the line.
     *
     * @return Line number.
     */
    int getLineNumber();
}
