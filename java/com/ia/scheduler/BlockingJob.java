package com.ia.scheduler;

import org.quartz.JobDataMap;

import com.google.inject.Inject;
import com.ia.crypto.CryptHandler;

/**
 * The base class for handling blocking script-execution jobs. Derived classes should call
 * executeNow(), which will bypass Job Scheduler and execute the script in blocking fashion.
 */
public class BlockingJob extends NativeJobHandler {

    private String dataDir;

    private Object params;

    private String token;

    @Inject
    public BlockingJob(final CryptHandler cryptHandler, final KJobScheduler kJobScheduler) {
        super(cryptHandler, kJobScheduler);
    }

    protected int executeNow(final String scriptName, final Object params, final String token,
            final String dataDir) {
        this.params = params;
        this.token = token;
        this.dataDir = dataDir;
        final JobDataMap dataMap = new JobDataMap();
        dataMap.put(DATA_DIR_NAME, dataDir);
        dataMap.put(SCRIPT_PARAMS, params);
        dataMap.put(TOKEN_NAME, token);
        dataMap.put(JOB_NAME, scriptName);
        dataMap.put(SCRIPT_NAME, scriptName);
        // Blocking jobs can neither be postponed nor be dropped
        return runNativeCommand(dataMap);
    }

    public String getDataDir() {
        return dataDir;
    }

    public Object getParams() {
        return params;
    }

    public String getToken() {
        return token;
    }

    public void setDataDir(final String dataDir) {
        this.dataDir = dataDir;
    }

    public void setParams(final Object params) {
        this.params = params;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
