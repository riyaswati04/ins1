package com.ia.scheduler;

import static com.ia.log.LogUtil.getLogger;
import static com.ia.scheduler.enums.JOB_EXECUTOR.PYTHON;
import static java.lang.Thread.sleep;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Inject;
import com.ia.crypto.CryptHandler;
import com.ia.log.Logger;
import com.ia.scheduler.enums.JOB_EXECUTOR;
import com.ia.util.TempFileUtil;

/**
 * The base class for handling script-execution jobs.
 */
public abstract class NativeJob implements IKJob, Comparable<NativeJob> {

    public static final String DATA_DIR_NAME = "datadir";

    public static final String FILES_TO_UPDLOAD = "uploadfiles";

    public static final String JOB_NAME = "job";

    public static final String OUT_FILE_NAME = "out";

    public static final String ParamMarker = "#PARAMS#";

    private static String perlCommand;

    private static String pythonCommand;

    private static int jobExecutionIdCounter = 0;

    public static final String SCRIPT_NAME = "script";

    public static final String SCRIPT_PARAMS = "PARAMS";

    public static final String TRANSACTION_ID = "token";

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

    protected String jobType;

    private final CryptHandler cryptHandler;

    protected JobDataMap dataMap;

    private JOB_EXECUTOR jobExecutor = PYTHON;

    public String jobName;

    private final KJobScheduler kJobScheduler;

    private final Logger logger = getLogger(getClass());

    protected File outFile;

    public String scriptName;

    protected File tempDir;

    protected int jobExecutionId = 0;

    /**
     * Empty constructor for job initilization
     *
     * <p>
     * Quartz requires a public empty constructor so that the scheduler can instantiate the class
     * whenever it needs.
     * </p>
     */
    @Inject
    public NativeJob(final CryptHandler cryptHandler, final KJobScheduler kJobScheduler) {
        this.cryptHandler = cryptHandler;
        this.kJobScheduler = kJobScheduler;
    }

    protected int abortPreProcess() {
        if (tempDir != null && !TempFileUtil.deleteTempDir(tempDir)) {
            logWarn("Could not delete temporary directory");
        }

        return 1;
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final JobDetail jobDetail = context.getJobDetail();
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        final String scriptName = dataMap.getString(SCRIPT_NAME);

        if (isEmpty(scriptName)) {
            this.dataMap = dataMap;
            jobName = dataMap.getString(JOB_NAME);
            tempDir = new File(dataMap.getString(DATA_DIR_NAME));
            outFile = new File(tempDir, OUT_FILE_NAME);
            postProcess(outFile);
        }
        else {
            runNativeCommand(dataMap);
        }
    }

    protected int getJobExecutionId() {
        if (jobExecutionId == 0) {
            jobExecutionId = ++jobExecutionIdCounter;
        }
        return jobExecutionId;
    }

    public String getJobType() {
        return jobType;
    }

    private String getLogMessage(final String message) {
        final StringBuilder sb = new StringBuilder(message);
        sb.append("[" + jobName + ",script=").append(scriptName).append(",dir=").append(tempDir)
                .append(']');
        return sb.toString();
    }

    protected void jobFailed(final int errorType) {
        JobStatistics.getJobStatistics().jobFailed(jobType, getJobExecutionId(), errorType);
    }

    public void logDebug(final String message) {
        logger.debug(getLogMessage(message));
    }

    public void logError(final String message) {
        logger.error(getLogMessage(message));
    }

    public void logError(final String message, final Exception exception) {
        logger.error(exception, getLogMessage(message));
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

    protected int postProcess(final File outFile) {
        return 0; // default implementation, always returns success
    }

    protected int preProcess() {
        return 0;
    }

    public int runNativeCommand(final JobDataMap dataMap) {
        int retCode = 1;
        long startTime = 0L;
        this.dataMap = dataMap;

        Process proc = null;

        try {
            jobName = dataMap.getString(JOB_NAME);
            scriptName = dataMap.getString(SCRIPT_NAME);
            final String transactionId = dataMap.getString(TRANSACTION_ID);
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

            // Feeds code + params and consumes the stdout+stderr from the process
            final StreamConsumer stdouterrConsumer = new StreamConsumer(scriptName, dataMap,
                    new ByteArrayInputStream(cryptHandler.getCode(scriptName)),
                    proc.getInputStream(), proc.getOutputStream(), saveOutputOS, jobExecutor,
                    dataMap.get(SCRIPT_PARAMS));

            stdouterrConsumer.start();

            retCode = proc.waitFor();

            int count = 0;
            while (stdouterrConsumer.isStreamConsumerRunning()) {
                if (++count > 5) {
                    break;
                }
                try {
                    sleep(2000); // 2 seconds
                }
                catch (final InterruptedException e) {
                    break;
                }
            }
            saveOutputOS.close();

            logInfo("Finished running (exitCode=" + retCode + ")");

            // Post-processing
            retCode = postProcess(outFile);
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
