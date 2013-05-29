package org.campagnelab.stepslogger;

import org.junit.Test;

/**
 * @author Fabien Campagne
 *         Date: 5/29/13
 *         Time: 11:01 AM
 */
public class StepLogToolTest {

    @Test
    // Check that the JSAP file is valid:
    public void testJSap() throws Exception {
        StepsLogTool.parseJSap(new String[]{});

    }
}
