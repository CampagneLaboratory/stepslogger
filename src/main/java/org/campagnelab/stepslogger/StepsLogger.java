package org.campagnelab.stepslogger;

import java.io.IOException;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:07 AM
 */
public interface StepsLogger {
    /**
     * Record that a step executed, successfully or not.
     *
     * @param message
     */
    public void step(String message);

    /**
     * Record the execution of a process, with standard out and standard error.  Call this method
     * before executing a process. Write to the redirect streams to record stdout or stderr of the process.
     *
     * @param message
     */
    public RedirectStreams stepProcess(String message, String command);

    /**
     * Call this method after a process finished. You must have called stepProcess before starting the process.
     * @param statusCode
     */
    public void processReturned(int statusCode);

    /**
     * Record an object was seen.
     *
     * @param name
     * @param dataPayload
     */

    public void observed(String name, String dataPayload);

    /**
     * Checks the step code and logs an error if statusCode is not zero.
     *
     * @param message
     * @param statusCode
     */
    public void step(String message, int statusCode);

    void close() throws IOException;
}
