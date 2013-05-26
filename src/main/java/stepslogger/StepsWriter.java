package stepslogger;

import org.campagnelab.stepslogger.Logformat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:27 AM
 */
public class StepsWriter {

    private Logformat.Log.Builder log = Logformat.Log.newBuilder();
    private FileOutputStream fos;
    private Logformat.Step.Builder latestStep;

    public StepsWriter(FileOutputStream stream) {
        fos = stream;
    }

    /**
     * Append a message to the log.
     *
     * @param message
     */
    public Logformat.Step.Builder newStep(String message) {
        Logformat.Step.Builder step = prepareNextStep();
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
    public Logformat.Step.Builder newStep(String message, int statusCode) {
        return newStep(message).setStatusCode(statusCode);
    }

    private Logformat.Step.Builder prepareNextStep() {
        final Logformat.Step.Builder builder = Logformat.Step.newBuilder();
        latestStep = builder;
        Logformat.Step.Builder step = builder;
        return step;
    }


    public Logformat.Step.Builder error(String message, int statusCode) {
        return newStep(message, statusCode);

    }

    public void append(Logformat.Step.Builder step) {
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

        Logformat.ObjectPayLoad.Builder observedBuilder = Logformat.ObjectPayLoad.newBuilder();
        observedBuilder.setObjectName(name);
        observedBuilder.setStringValue(payLoad);
        latestStep.addObservedObjects(observedBuilder);
    }


    public void append(RedirectStreams latestRedirect, int statusCode) {
        latestStep.setStdout(latestRedirect.getStandardOutAsString());
        latestStep.setStderr(latestRedirect.getStandardErrAsString());
        latestStep.setStatusCode(statusCode);
        append(latestStep);

    }

    public void flush() {
        if (latestStep != null) {
            append(latestStep);
        }
    }

    public void close() throws IOException {
        flush();
        log.build().writeDelimitedTo(fos);
        fos.close();
    }

}
