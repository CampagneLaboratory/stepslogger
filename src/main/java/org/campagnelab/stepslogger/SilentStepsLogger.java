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
