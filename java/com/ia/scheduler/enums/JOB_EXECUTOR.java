package com.ia.scheduler.enums;

import static com.ia.common.IAProperties.bashCommand;
import static com.ia.common.IAProperties.perlCommand;
import static com.ia.common.IAProperties.pythonCommand;

public enum JOB_EXECUTOR {

    BASH(bashCommand),

    NONE(""),

    PERL(perlCommand),

    PYTHON(pythonCommand),

    SQL("");

    private final String executionPath;

    private JOB_EXECUTOR(final String executionPath) {
        this.executionPath = executionPath;
    }

    public String getExecutionPath() {
        return executionPath;
    }
}
