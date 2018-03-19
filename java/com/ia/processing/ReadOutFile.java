package com.ia.processing;

import java.io.File;
import java.util.Queue;

public interface ReadOutFile {

    Queue<ScriptOutputLine> readLines(File file);
}
