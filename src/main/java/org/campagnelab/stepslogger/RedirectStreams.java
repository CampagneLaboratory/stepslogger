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
