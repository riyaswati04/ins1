package com.ia.scheduler;

import static com.ia.log.LogUtil.getLogger;
import static com.ia.scheduler.enums.JOB_EXECUTOR.PYTHON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.quartz.JobDataMap;

import com.google.gson.Gson;
import com.ia.log.Logger;
import com.ia.scheduler.enums.JOB_EXECUTOR;

public class StreamConsumer extends Thread {

    private static final String PARAMMARKER = "#PARAMS#";

    protected InputStream codeIS, suckOutputIS;

    protected JobDataMap dataMap;

    protected OutputStream feedCodeOS, saveOutputOS;

    protected JOB_EXECUTOR jobExecutor;

    protected Object params;

    protected String scriptName;

    protected boolean streamConsumerRunning = false;

    private final Logger logger = getLogger(getClass());

    public StreamConsumer(final String scriptName, final JobDataMap dataMap,
            final InputStream codeIS, final InputStream suckOutputIS, final OutputStream feedCodeOS,
            final OutputStream saveOutputOS, final JOB_EXECUTOR jobExecutor, final Object params) {
        super("Job-" + scriptName);
        this.scriptName = scriptName;
        this.dataMap = dataMap;
        this.codeIS = codeIS;
        this.suckOutputIS = suckOutputIS;
        this.feedCodeOS = feedCodeOS;
        this.saveOutputOS = saveOutputOS;
        this.jobExecutor = jobExecutor;
        this.params = params;
        streamConsumerRunning = true;
    }

    public boolean isStreamConsumerRunning() {
        return streamConsumerRunning;
    }

    @Override
    public void run() {
        final PrintWriter saveOutputPW = new PrintWriter(saveOutputOS, true);
        PrintWriter feedCodePW = new PrintWriter(feedCodeOS, true);
        BufferedReader suckOutputBR = null;
        final String scriptParams = new Gson().toJson(params);

        logger.info("Params is " + scriptParams);
        try {
            // First feed the entire code, substituting params when reqd
            String line = null;
            boolean paramsDone = false;
            String code = "";
            switch (jobExecutor) {
                case PYTHON:
                    code = "main('" + scriptParams + "')";
                    paramsDone = true;
                    break;
                case BASH:
                    code = "main " + scriptParams;
                    paramsDone = true;
                    break;
                case PERL:
                    code = "my $file=" + scriptParams + ';';
                    break;
                default:
                    break;
            }

            final BufferedReader codeBR = new BufferedReader(new InputStreamReader(codeIS));
            while ((line = codeBR.readLine()) != null) {
                if (!paramsDone) {
                    if (line.equals(PARAMMARKER)) {
                        feedCodePW.println(code);
                        paramsDone = true;
                        continue;
                    }
                }
                feedCodePW.println(line);
            }
            /* For Python last line should be execution */
            if (jobExecutor == PYTHON) {
                feedCodePW.println(code);
            }
            codeBR.close();
            feedCodePW.close();
            feedCodePW = null;

            // Now suck out stdout and stderr
            suckOutputBR = new BufferedReader(new InputStreamReader(suckOutputIS));
            while ((line = suckOutputBR.readLine()) != null) {
                saveOutputPW.println(line);
            }
        }
        catch (final IOException e) {
            logger.error("Error consuming stdout/stderr stream of spawned process", e);
        }
        catch (final Exception ex) {
            logger.error("Error during the run of spawned process", ex);
        }
        finally {
            if (suckOutputBR != null) {
                try {
                    suckOutputBR.close();
                }
                catch (final Exception ignore) {
                }
            }
            if (feedCodePW != null) {
                try {
                    feedCodePW.close();
                }
                catch (final Exception ignore) {
                }
            }
        }
        streamConsumerRunning = false;
    }

    public void setStreamConsumerRunning(final boolean streamConsumerRunning) {
        this.streamConsumerRunning = streamConsumerRunning;
    }

}
