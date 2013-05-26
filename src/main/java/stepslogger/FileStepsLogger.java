package stepslogger;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:00 AM
 */
public class FileStepsLogger implements StepsLogger {
    private static final Logger LOG = Logger.getLogger(RedirectStreams.class);

    public FileStepsLogger(File logDirectory) {
        assert logDirectory.isDirectory();
        File logFile = new File(FilenameUtils.concat(logDirectory.getAbsolutePath(), String.format("log-%d.slog",
                new Date().getTime())));

        try {
            if (logFile.createNewFile()) {
                writer = new StepsWriter(new FileOutputStream(logFile));
            } else {
                LOG.error("Unable to create the step logger file: " + logFile);
            }
        } catch (IOException e) {
            LOG.error("Unable to create the step logger file: " + logFile);
        }
        assert writer != null : "The writer must be created.";
    }

    private StepsWriter writer;

    /**
     * Record that a step executed, successfully or not.
     *
     * @param message
     */
    public void step(String message) {
        writer.append(message);
    }

    RedirectStreams latestRedirect;

    @Override
    public RedirectStreams stepProcess(String message, String command) {
        latestRedirect = new RedirectStreams(redirectBufferSize);
        writer.appendStepProcess(message, command);
        return latestRedirect;
    }

    public void processReturned(int statusCode) {
        writer.append(latestRedirect, statusCode);
        latestRedirect = null;
    }

    /**
     * Record an object was seen.
     *
     * @param name
     * @param dataPayload
     */

    public void observed(String name, String dataPayload) {
        writer.observe(name, dataPayload);
    }

    /**
     * Checks the step code and logs an error if statusCode is not zero.
     *
     * @param message
     * @param statusCode
     */
    public void step(String message, int statusCode) {

        writer.append(message, statusCode);

    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private int redirectBufferSize=10000;

    /**
     * Set the maximum size of the process stderr and stdout capture buffers.
     * @param size Size of the buffers in bytes.
     */
    public void setRedirectBufferSize(int size) {
        redirectBufferSize = size;
    }
}
