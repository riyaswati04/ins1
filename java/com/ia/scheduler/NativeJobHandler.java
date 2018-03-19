package com.ia.scheduler;

import static com.ia.log.LogUtil.getLogger;
import static com.ia.scheduler.enums.JOB_EXECUTOR.PYTHON;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Inject;
import com.ia.crypto.CryptHandler;
import com.ia.log.Logger;
import com.ia.scheduler.enums.JOB_EXECUTOR;

/**
 * The class for handling script-execution jobs.
 */
public abstract class NativeJobHandler implements Job {
    private static String pythonCommand;

    private static String perlCommand;

    public static final String DATA_DIR_NAME = "datadir";

    public static final String FILES_TO_UPDLOAD = "uploadfiles";

    public static final String JOB_NAME = "job";

    public static final String OUT_FILE_NAME = "out";

    public static final String ParamMarker = "#PARAMS#";

    public static final String SCRIPT_NAME = "script";

    public static final String SCRIPT_PARAMS = "PARAMS";

    public static final String TOKEN_NAME = "token";

    public static void init(final String pyCommand, final String perlCmd) throws IOException {
        pythonCommand = pyCommand;
        perlCommand = perlCmd;

        File f = new File(pythonCommand);
        if (!f.exists()) {
            throw new IOException("Script command " + pythonCommand + " is not found!");
        }
        if (!f.isFile()) {
            throw new IOException("Script command " + pythonCommand + " is not a file!");
        }

        f = new File(perlCommand);
        if (!f.exists()) {
            throw new IOException("Script command " + perlCommand + " is not found!");
        }
        if (!f.isFile()) {
            throw new IOException("Script command " + perlCommand + " is not a file!");
        }
    }

    private final CryptHandler cryptHandler;

    private JOB_EXECUTOR jobExecutor = PYTHON;

    public String jobName;

    private final KJobScheduler kJobScheduler;

    private final Logger logger = getLogger(getClass());

    protected File outFile;

    public String scriptName;

    private File tempDir;

    /**
     * Empty constructor for job initilization
     *
     * <p>
     * Quartz requires a public empty constructor so that the scheduler can instantiate the class
     * whenever it needs.
     * </p>
     */
    @Inject
    public NativeJobHandler(final CryptHandler cryptHandler, final KJobScheduler kJobScheduler) {
        this.cryptHandler = cryptHandler;
        this.kJobScheduler = kJobScheduler;
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final JobDetail jobDetail = context.getJobDetail();
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        runNativeCommand(dataMap);
    }

    private String getLogMessage(final String message) {
        final StringBuilder sb = new StringBuilder(message);
        sb.append("[" + jobName + ",script=").append(scriptName).append(",dir=").append(tempDir)
                .append(']');
        return sb.toString();
    }

    public void logDebug(final String message) {
        logger.debug(getLogMessage(message));
    }

    public void logError(final String message) {
        logger.error(getLogMessage(message));
    }

    public void logError(final String message, final Exception exception) {
        logger.error(getLogMessage(message), exception);
    }

    public void logInfo(final String message) {
        logger.info(getLogMessage(message));
    }

    public void logWarn(final String message) {
        logger.warn(getLogMessage(message));
    }

    public void logWarn(final String message, final Exception exception) {
        logger.warn(getLogMessage(message), exception);
    }

    public int runNativeCommand(final JobDataMap dataMap) {
        int retCode = 1;
        long startTime = 0L;

        Process proc = null;

        try {
            jobName = dataMap.getString(JOB_NAME);
            scriptName = dataMap.getString(SCRIPT_NAME);
            // Create directory $scriptExecDir/$dirName for job execution

            tempDir = new File(dataMap.getString(DATA_DIR_NAME));

            logInfo("About to run");

            // Both the stdout and stderr of the job should go to a file called
            // "out" in the tempDir
            outFile = new File(tempDir, OUT_FILE_NAME);
            final FileOutputStream saveOutputOS = new FileOutputStream(outFile);
            final ProcessBuilder pb =
                    new ProcessBuilder(jobExecutor == PYTHON ? pythonCommand : perlCommand);
            pb.redirectErrorStream(true);
            pb.directory(tempDir);
            proc = pb.start();
            startTime = kJobScheduler.processStarted(proc);

            // Feeds code+params and consumes the stdout+stderr from the process
            final StreamConsumer stdouterrConsumer = new StreamConsumer(scriptName, dataMap,
                    new ByteArrayInputStream(cryptHandler.getCode(scriptName)),
                    proc.getInputStream(), proc.getOutputStream(), saveOutputOS, jobExecutor,
                    dataMap.get(SCRIPT_PARAMS));

            stdouterrConsumer.start();

            retCode = proc.waitFor();

            saveOutputOS.close();

            logInfo("Finished running (exitCode=" + retCode + ")");
        }
        catch (final Exception e) {
            try {
                logError("Error in execution of native job", e);
            }
            catch (final Exception ee) {
                logError("Exception during status message handling after failure of native job",
                        ee);
            }
            retCode = 1;
        }
        finally {
            if (proc != null) {
                closeQuietly(proc.getInputStream());
                closeQuietly(proc.getOutputStream());
                closeQuietly(proc.getErrorStream());
            }
            if (startTime != 0) {
                kJobScheduler.processEnded(startTime);
            }
        }
        return retCode;
    }

    public void setJobExecutor(final JOB_EXECUTOR jobExecutor) {
        this.jobExecutor = jobExecutor;
    }
}
