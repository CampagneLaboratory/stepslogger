package org.campagnelab.stepslogger;

import campagnelab.stepslogger.LogFormat;
import campagnelab.stepslogger.LogFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:27 AM
 */
public class StepsWriter {

    private LogFormat.Log.Builder log = LogFormat.Log.newBuilder();
    private FileOutputStream fos;
    private LogFormat.Step.Builder latestStep;

    public StepsWriter(FileOutputStream stream) {
        fos = stream;
    }

    /**
     * Append a message to the log.
     *
     * @param message
     */
    public LogFormat.Step.Builder newStep(String message) {
        LogFormat.Step.Builder step = prepareNextStep();
        Date currentTime = new Date();
        step.setTimeStamp(currentTime.getTime());
        step.setTheMessage(message);

        return step;
    }

    /**
     * Append a message to the log.
     *
     * @param message
     */
    public LogFormat.Step.Builder newStep(String message, int statusCode) {
        return newStep(message).setStatusCode(statusCode);
    }

    private LogFormat.Step.Builder prepareNextStep() {
        final LogFormat.Step.Builder builder = LogFormat.Step.newBuilder();
        latestStep = builder;
        LogFormat.Step.Builder step = builder;
        return step;
    }


    public LogFormat.Step.Builder error(String message, int statusCode) {
        return newStep(message, statusCode);

    }

    public void append(LogFormat.Step.Builder step) {
        if (!step.hasError()) {
            if (step.hasStatusCode()) {

                step.setError(step.getStatusCode() != 0);
            } else {
                step.setError(false);
            }
        }
        log.addSteps(step);
        latestStep = null;
    }

    public void append(String message) {
        append(newStep(message));

    }

    public void append(String message, int statusCode) {
        append(newStep(message, statusCode));
    }

    public void observe(String name, String payLoad) {
        assert latestStep != null;

        LogFormat.ObjectPayLoad.Builder observedBuilder = LogFormat.ObjectPayLoad.newBuilder();
        observedBuilder.setObjectName(name);
        observedBuilder.setStringValue(payLoad);
        latestStep.addObservedObjects(observedBuilder);
    }


    public void append(RedirectStreams latestRedirect, int statusCode) {
        if (latestStep != null) {
            latestStep.setStdout(latestRedirect.getStandardOutAsString());
            latestStep.setStderr(latestRedirect.getStandardErrAsString());
            latestStep.setStatusCode(statusCode);
            append(latestStep);
        }
    }

    public void flush() {
        if (latestStep != null) {
            append(latestStep);
            latestStep=null;
        }
    }

    public void close() throws IOException {
        flush();
        log.build().writeDelimitedTo(fos);
        fos.close();
    }

    public void appendStepProcess(String message, String command) {
        latestStep=newStep(message).setCommand(command);
    }
}
