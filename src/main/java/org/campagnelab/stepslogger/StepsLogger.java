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

import java.io.Closeable;
import java.io.IOException;
import java.io.Closeable;
/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:07 AM
 */
public interface StepsLogger extends Closeable {
    /**
     * Record that a step executed, successfully or not.
     *
     * @param message
     */
    public void step(String message);

    /**
     * Record that a step could not be performed.
     *
     * @param message
     */
    public void error(String message);

    /**
     * Record the execution of a process, with standard out and standard error.  Call this method
     * before executing a process. Write to the redirect streams to record stdout or stderr of the process.
     *
     * @param message
     */
    public RedirectStreams stepProcess(String message, String command);

    /**
     * Call this method after a process finished. You must have called stepProcess before starting the process.
     *
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
