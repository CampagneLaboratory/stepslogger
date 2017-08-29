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

import org.apache.log4j.Logger;
import org.campagnelab.stepslogger.util.CircularByteArrayOutputStream;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:08 AM
 */
public class RedirectStreams {

    private static final Logger LOG = Logger.getLogger(RedirectStreams.class);
    private final CircularByteArrayOutputStream bos;
    private final CircularByteArrayOutputStream bes;

    public RedirectStreams(int redirectBufferSize) {
        final int BUFFER_SIZE = redirectBufferSize;
        bos = new CircularByteArrayOutputStream(BUFFER_SIZE);
        bes = new CircularByteArrayOutputStream(BUFFER_SIZE);
    }

    public RedirectStreams() {
        this(10000);
    }

    public OutputStream getStandardOut() {
        return bos;
    }

    public OutputStream getStandardError() {
        return bes;
    }

    public String getStandardOutAsString() {
        try {
            return new String(bos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            final String message = "Unsupported encoding when converting stdout to a string";
            LOG.error(message);
            throw new InternalError(message);
        }
    }

    public String getStandardErrAsString() {
        try {
            return new String(bes.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            final String message = "Unsupported encoding when converting stdout to a string";
            LOG.error(message);
            throw new InternalError(message);
        }
    }
}
