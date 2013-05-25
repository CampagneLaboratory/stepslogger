package stepslogger;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * @author Fabien Campagne
 *         Date: 5/25/13
 *         Time: 10:08 AM
 */
public class RedirectStreams {
    private static final int BUFFER_SIZE = 1000000;
    private static final Logger LOG = Logger.getLogger(RedirectStreams.class);
    private ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE);
    private ByteArrayOutputStream bes = new ByteArrayOutputStream(BUFFER_SIZE);

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
