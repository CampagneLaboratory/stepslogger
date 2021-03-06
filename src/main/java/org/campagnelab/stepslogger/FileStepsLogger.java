/*
 * Copyright (c) [2017] [Weill Cornell Medical College]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.campagnelab.stepslogger;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:00 AM
 */
public class FileStepsLogger implements StepsLogger {
    private static final Logger LOG = Logger.getLogger(FileStepsLogger.class);

    public FileStepsLogger(File logDirectory) {
        assert logDirectory.isDirectory() : "parameter must be a directory.";
        String logFilename = String.format("log-%d.slog",
                new Date().getTime());
        File logFile = new File(FilenameUtils.concat(logDirectory.getAbsolutePath(), logFilename));
        LOG.info("Creating stepslogger logFile " + logFile);
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

    @Override
    public void error(String message) {
        step(message, 1);
    }

    RedirectStreams latestRedirect;

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


    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

    private int redirectBufferSize = 10000;

    /**
     * Set the maximum size of the process stderr and stdout capture buffers.
     *
     * @param size Size of the buffers in bytes.
     */
    public void setRedirectBufferSize(int size) {
        redirectBufferSize = size;
    }
}
