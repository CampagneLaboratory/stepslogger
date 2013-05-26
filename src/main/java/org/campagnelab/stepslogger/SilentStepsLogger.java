package org.campagnelab.stepslogger;

import java.io.IOException;

/**
 * An implementation that does not log. It is completely silent.
 * @author Fabien Campagne
 *         Date: 5/26/13
 *         Time: 3:04 PM
 */
public class SilentStepsLogger implements StepsLogger {
    @Override
    public void step(String message) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public RedirectStreams stepProcess(String message, String command) {
        return new RedirectStreams(100);
    }

    @Override
    public void processReturned(int statusCode) {

    }

    @Override
    public void observed(String name, String dataPayload) {

    }

    @Override
    public void step(String message, int statusCode) {

    }

    @Override
    public void close() throws IOException {

    }
}
